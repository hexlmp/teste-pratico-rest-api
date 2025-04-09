Ext.define('App.controller.ServidoresController', {
    extend: 'Ext.app.Controller',
    alias: 'controller.servidores',

    views: [
        'servidores.ServidoresEfetivosGrid',
        'servidores.ServidorEfetivoForm',
        'servidores.ServidorTemporarioForm',
        'servidores.ServidoresTemporariosGrid'
    ],

    stores: [
        'ServidoresEfetivos',
        'Pessoas'
    ],

    init: function() {
        this.control({
            'servidores-efetivos-grid': {
                onAddServidorEfetivo: this.onAddServidorEfetivo,
                onEditServidorEfetivo: this.onEditServidorEfetivo,
                onRemoveServidorEfetivo: this.onRemoveServidorEfetivo
            },
            'servidor-efetivo-form': {
                onSaveServidorEfetivo: this.onSaveServidorEfetivo,
                onCancelServidorEfetivo: this.onCancelServidorEfetivo
            }
        });
    },

    /*
        ####################
        Servidor Efetivo
        ####################
    */
    onAddServidorEfetivo: function() {
        var view = Ext.widget('servidor-efetivo-form'/*, {
            title: 'Adicionar Servidor Efetivo'
        }*/);

        Ext.create('Ext.window.Window', {
            title: 'Cadastrar Servidor Efetivo',
            width: 600,
            height: 450,
            layout: 'fit',
            modal: true,
            items: view,
            listeners: {
                close: function(win) {
                    // Recarrega ambos os stores
                    //Ext.getStore('PessoasCombo').load();
                    var store = Ext.getStore('PessoasCombo');
                    store.clearFilter();
                    store.load();
                    win.destroy(); // Destrói a janela ao fechá-la
                }
            }
        }).show();
    },

    onEditServidorEfetivo: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];
        var view = Ext.widget('servidor-efetivo-form');
        //console.log(record);
        //console.log(record.get('pessoa').nome);
        //console.log(record.data.pessoa.nome);
        //console.log(record.data.pessoa.id);
        var win = Ext.create('Ext.window.Window', {
            title: 'Editar Servidor Efetivo  (' + record.get('matricula') + ' - ' +record.get('pessoa').nome + ')',
            width: 600,
            height: 450,
            layout: 'fit',
            modal: true,
            items: view,
            listeners: {
                close: function(win) {
                    // Recarrega store
                    var store = Ext.getStore('PessoasCombo');
                    store.clearFilter();
                    store.load();
                    win.destroy(); // Destrói a janela ao fechá-la
                }
            }
        });

        win.show();
        view.loadRecord(record);

    },

    onRemoveServidorEfetivo: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];
        var pessoaId = record.get('pessoa').id;
        var matricula = record.get('matricula');

        Ext.Msg.confirm('Confirmação', 'Deseja realmente remover o servidor ' + matricula + '?', function(btn) {
            if (btn === 'yes') {
                Ext.Ajax.request({
                    url: '/protected/api/servidores-efetivos',
                    method: 'DELETE',
                    params: {
                        pessoaId: pessoaId,
                        matricula: matricula
                    },
                    success: function() {
                        grid.getStore().load();
                        Ext.toast({
                            html: 'Servidor removido com sucesso',
                            timeout: 3000
                        });
                    },
                    failure: function(response) {
                        var error = Ext.JSON.decode(response.responseText);
                        Ext.toast({
                            html: error.message || 'Erro ao remover servidor',
                            timeout: 3000,
                            cls: 'toast-error'
                        });
                    }
                });
            }
        });
    },
    onSaveServidorEfetivo: function(button) {
        var form = button.up('form');
        var values = form.getValues(); /*form.getViewModel().getData();*/
        var record = form.getRecord();
        var isNew = !record;

        //console.log(values);
        //console.log(record);
        //console.log(form.getViewModel().getData());

        // Confirmação antes de salvar
        Ext.Msg.confirm('Confirmação', 'Deseja realmente ' + (isNew ? 'cadastrar' : 'atualizar') + ' este servidor?', function(btn) {
            if (btn === 'yes') {
                // Configura a requisição
                var url = '/protected/api/servidores-efetivos';
                var method = isNew ? 'POST' : 'PUT';

                // Prepara os dados para envio
                var params = {
                    matricula: values.matricula,
                    pessoa: values.pessoa
                };

                // Para atualização, adiciona os campos antigos
                if (!isNew) {
                    params.matriculaAntiga = values.matriculaAntiga;
                    params.pessoaAntiga = values.pessoaAntiga;
                }

                // Envia a requisição
                Ext.Ajax.request({
                    url: url,
                    method: method,
                    jsonData: params, // Envia como JSON no body
                    success: function(response) {
                        var grid = Ext.ComponentQuery.query('servidores-efetivos-grid')[0];
                        grid.getStore().load();
                        form.up('window').close();
                        Ext.toast({
                            html: 'Servidor ' + (isNew ? 'cadastrado' : 'atualizado') + ' com sucesso',
                            timeout: 3000
                        });

                    },
                    failure: function(response) {
                        var error = Ext.JSON.decode(response.responseText);
                        Ext.toast({
                            html: error.message || ('Erro ao ' + (isNew ? 'cadastrar' : 'atualizar') + ' servidor'),
                            timeout: 3000,
                            cls: 'toast-error',
                            //iconCls: 'x-fa fa-exclamation-triangle' // Ícone de erro
                        });

                    }
                });
            }
        });
    },

    onCancelServidorEfetivo: function(button) {
        button.up('window').close();
    },
    /*
        ####################
        Servidor Temporário
        ####################
    */
    onAddServidorTemporario: function() {
        var view = Ext.widget('servidor-temporario-form', {
            title: 'Adicionar Servidor Temporário'
        });

        Ext.create('Ext.window.Window', {
            title: 'Novo Servidor Temporário',
            width: 500,
            layout: 'fit',
            modal: true,
            items: view
        }).show();
    },

    onEditServidorTemporario: function(grid) {
        //var grid = Ext.ComponentQuery.query('servidores-temporarios-grid')[0];
        var record = grid.getSelectionModel().getSelection()[0];
        var view = Ext.widget('servidor-temporario-form');

        var win = Ext.create('Ext.window.Window', {
            title: 'Editar Servidor Temporário  ('+record.get('pessoa').nome + ')',
            width: 500,
            layout: 'fit',
            modal: true,
            items: view,
            listeners: {
                close: function() {
                    // Recarrega store
                    var store = Ext.getStore('Pessoas');
                    store.clearFilter();
                    store.load();
                }
            }
        });

        view.loadRecord(record);
        win.show();
    },

    onRemoveServidorTemporario: function() {
        var grid = Ext.ComponentQuery.query('servidores-temporarios-grid')[0];
        var record = grid.getSelectionModel().getSelection()[0];

        Ext.Msg.confirm('Confirmação', 'Deseja realmente remover este servidor temporário?', function(btn) {
            if (btn === 'yes') {
                Ext.Ajax.request({
                    url: '/protected/api/servidores-temporarios',
                    method: 'DELETE',
                    params: {
                        pessoaId: record.get('pessoa').id,
                        dataAdmissao: Ext.Date.format(record.get('dataAdmissao'), 'Y-m-d')
                    },
                    success: function() {
                        grid.getStore().load();
                        Ext.toast({
                            html: 'Servidor removido com sucesso',
                            timeout: 3000
                        });
                    },
                    failure: function(response) {
                        var error = Ext.JSON.decode(response.responseText);
                        Ext.toast({
                            html: error.message || 'Erro ao remover servidor',
                            timeout: 3000,
                            cls: 'toast-error'
                        });
                    }
                });
            }
        });
    },

    onSaveServidorTemporario: function(button) {
        var form = button.up('form');
        var values = form.getValues();
        var isNew = !form.getRecord();

        console.log(values);
        Ext.Msg.confirm('Confirmação', 'Deseja realmente salvar?', function(btn) {
            if (btn === 'yes') {
                var url = '/protected/api/servidores-temporarios';
                var method = isNew ? 'POST' : 'PUT';
                var params = {
                    pessoa: values.pessoa,
                    dataAdmissao: values.dataAdmissao,
                    dataDemissao: values.dataDemissao
                };

                if (!isNew) {
                    params.pessoaAntiga = values.pessoaAntiga;
                    params.dataAdmissaoAntiga = values.dataAdmissaoAntiga;
                }

                Ext.Ajax.request({
                    url: url,
                    method: method,
                    jsonData: params,
                    success: function() {
                        var grid = Ext.ComponentQuery.query('servidores-temporarios-grid')[0];
                        grid.getStore().load();
                        form.up('window').close();
                        Ext.toast({
                            html: 'Servidor cadastrado com sucesso',
                            timeout: 3000
                        });

                    },
                    failure: function(response) {
                        var error = Ext.JSON.decode(response.responseText);
                        Ext.toast({
                            html: error.message || 'Erro ao cadastrar servidor',
                            timeout: 3000,
                            cls: 'toast-error'
                        });

                    }
                });
            }
        });
    },

    onCancelServidorTemporario: function(button) {
        button.up('window').close();
    }

});