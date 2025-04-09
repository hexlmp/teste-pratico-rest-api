Ext.define('App.controller.EnderecosController', {
    extend: 'Ext.app.Controller',
    alias: 'controller.enderecos',

    views: [
        'enderecos.EnderecosGrid',
        'enderecos.EnderecoForm'
    ],

    stores: [
        'Enderecos',
        'Cidades'
    ],

    refs: [{
        ref: 'enderecosGrid',
        selector: 'enderecos-grid'
    }],

    init: function() {
        this.control({
            'enderecos-grid': {
                onAddEndereco: this.onAddEndereco,
                onEditEndereco: this.onEditEndereco,
                onRemoveEndereco: this.onRemoveEndereco
            },
            'endereco-form': {
                onSaveEndereco: this.onSaveEndereco,
                onCancelEndereco: this.onCancelEndereco
            }
        });
    },

    onAddEndereco: function() {
        var view = Ext.widget('endereco-form');

        Ext.create('Ext.window.Window', {
            title: 'Novo Endereço',
            width: 500,
            height: 350,
            layout: 'fit',
            modal: true,
            items: view
        }).show();
    },

    onEditEndereco: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];
        var view = Ext.widget('endereco-form');

        var win = Ext.create('Ext.window.Window', {
            title: 'Editar Endereço - ' + record.get('logradouro'),
            width: 500,
            height: 350,
            layout: 'fit',
            modal: true,
            items: view
        });

        view.loadRecord(record);

        // Carrega a cidade selecionada
        var cidadeCombo = view.down('combobox[name=cidade]');
        if (record.get('cidade')) {
            cidadeCombo.getStore().load({
                scope: this,
                callback: function() {
                    cidadeCombo.setValue(record.get('cidade').id);
                }
            });
        }

        win.show();
    },

    onRemoveEndereco: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];

        Ext.Msg.confirm('Confirmação', 'Deseja realmente remover este endereço?', function(btn) {
            if (btn === 'yes') {
                grid.getStore().remove(record);
                grid.getStore().sync({
                    success: function() {
                        Ext.toast({
                            html: 'Endereço removido com sucesso',
                            timeout: 3000
                        });
                    },
                    failure: function() {
                        Ext.toast({
                            html: 'Erro ao remover endereço',
                            timeout: 3000,
                            ui: 'error'
                        });
                    }
                });
            }
        });
    },

    onSaveEndereco: function(button) {
        var form = button.up('form');
        var values = form.getValues();
        var record = form.getRecord();
        var isNew = !record;

        Ext.Msg.confirm('Confirmação', 'Deseja realmente ' + (isNew ? 'cadastrar' : 'atualizar') + ' este endereço?', function(btn) {
            if (btn === 'yes') {
                var url = '/protected/api/enderecos';
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

                        var grid = Ext.ComponentQuery.query('enderecos-grid')[0];
                        grid.getStore().load();

                        form.up('window').close();

                        Ext.toast({
                            html: 'Endereço ' + (isNew ? 'cadastrado' : 'atualizado') + ' com sucesso',
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
                        var errorMsg = 'Erro ao ' + (isNew ? 'cadastrar' : 'atualizar') + ' endereço';

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

    onCancelEndereco: function(button) {
        button.up('window').close();
    }
});