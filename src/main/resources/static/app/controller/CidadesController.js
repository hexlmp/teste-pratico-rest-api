Ext.define('App.controller.CidadesController', {
    extend: 'Ext.app.Controller',
    alias: 'controller.cidades',

    views: [
        'cidades.CidadesGrid',
        'cidades.CidadeForm'
    ],

    stores: [
        'Cidades'
    ],

    refs: [{
        ref: 'cidadesGrid',
        selector: 'cidades-grid'
    }],

    init: function() {
        this.control({
            'cidades-grid': {
                onAddCidade: this.onAddCidade,
                onEditCidade: this.onEditCidade,
                onRemoveCidade: this.onRemoveCidade
            },
            'cidade-form': {
                onSaveCidade: this.onSaveCidade,
                onCancelCidade: this.onCancelCidade
            }
        });
    },

    onAddCidade: function() {
        var view = Ext.widget('cidade-form');

        Ext.create('Ext.window.Window', {
            title: 'Nova Cidade',
            width: 400,
            height: 200,
            layout: 'fit',
            modal: true,
            items: view
        }).show();
    },

    onEditCidade: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];
        var view = Ext.widget('cidade-form');

        var win = Ext.create('Ext.window.Window', {
            title: 'Editar Cidade - ' + record.get('nome'),
            width: 400,
            height: 200,
            layout: 'fit',
            modal: true,
            items: view
        });

        view.loadRecord(record);
        win.show();
    },

    onRemoveCidade: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];

        Ext.Msg.confirm('Confirmação', 'Deseja realmente remover ' + record.get('nome') + '?', function(btn) {
            if (btn === 'yes') {
                grid.getStore().remove(record);
                grid.getStore().sync({
                    success: function() {
                        Ext.toast({
                            html: 'Cidade removida com sucesso',
                            timeout: 3000
                        });
                    },
                    failure: function() {
                        Ext.toast({
                            html: 'Erro ao remover cidade',
                            timeout: 3000,
                            ui: 'error'
                        });
                    }
                });
            }
        });
    },

    onSaveCidade: function(button) {
        var form = button.up('form');
        var values = form.getValues();
        var record = form.getRecord();
        var isNew = !record;

        Ext.Msg.confirm('Confirmação', 'Deseja realmente ' + (isNew ? 'cadastrar' : 'atualizar') + ' esta cidade?', function(btn) {
            if (btn === 'yes') {
                var url = '/protected/api/cidades';
                var method = isNew ? 'POST' : 'PUT';

                if (!isNew) {
                    url += '/' + values.id;
                }

                Ext.Ajax.request({
                    url: url,
                    method: method,
                    jsonData: values,
                    success: function(response) {
                        var data = Ext.decode(response.responseText);

                        var grid = Ext.ComponentQuery.query('cidades-grid')[0];
                        grid.getStore().load();

                        form.up('window').close();

                        Ext.toast({
                            html: 'Cidade ' + (isNew ? 'cadastrada' : 'atualizada') + ' com sucesso',
                            timeout: 3000
                        });

                        if (isNew && data.id) {
                            var newRecord = grid.getStore().getById(data.id);
                            if (newRecord) {
                                grid.getSelectionModel().select(newRecord);
                            }
                        }
                    },
                    failure: function(response) {
                        var errorMsg = 'Erro ao ' + (isNew ? 'cadastrar' : 'atualizar') + ' cidade';

                        try {
                            var errorResponse = Ext.decode(response.responseText);
                            if (errorResponse.message) {
                                errorMsg += ': ' + errorResponse.message;
                            }
                        } catch (e) {}

                        Ext.toast({
                            html: errorMsg,
                            timeout: 3000,
                            ui: 'error'
                        });
                    }
                });
            }
        });
    },

    onCancelCidade: function(button) {
        button.up('window').close();
    }
});