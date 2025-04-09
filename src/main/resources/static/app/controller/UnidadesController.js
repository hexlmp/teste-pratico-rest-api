Ext.define('App.controller.UnidadesController', {
    extend: 'Ext.app.Controller',

    stores: ['Unidades'],
    models: ['Unidade'],
    views: ['unidades.UnidadesGrid', 'unidades.UnidadeForm'],

    /*init: function() {
        this.control({
            'unidades-grid': {
                selectionchange: this.onSelectionChange
            }
        });
    },*/

    onAddUnidade: function() {
        var view = Ext.widget('unidade-form', {
            title: 'Adicionar Unidade'
        });
        this.showForm(view);
    },

    onEditUnidade: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];
        var view = Ext.widget('unidade-form', {
            title: 'Editar Unidade - ' + record.get('nome')
        });
        var form = view.getForm();
        form.loadRecord(record);

        this.carregarEnderecosVinculados(view, record['id']);

        this.showForm(view);
    },

    onRemoveUnidade: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];

        Ext.Msg.confirm('Confirmação', 'Deseja realmente remover a unidade ' + record.get('nome') + '?', function(btn) {
            if (btn === 'yes') {
                record.erase({
                    success: function() {

                        Ext.toast({
                            html: 'Unidade removida com sucesso!',
                            timeout: 3000
                        });
                        grid.getStore().reload();
                    },
                    failure: function() {
                        Ext.toast({
                            html: 'Erro ao remover unidade!',
                            timeout: 3000,
                            ui: 'error'
                        });
                    }
                });
            }
        });
    },

    onCancelUnidade: function(btn) {
        btn.up('window').close();
    },

    showForm: function(form) {
        var win = Ext.create('Ext.window.Window', {
            title: form.getTitle(),
            modal: true,
            width: 500,
            height: 450,
            layout: 'fit',
            items: form,
            listeners: {
                close: function(win) {
                    win.destroy(); // Destrói a janela ao fechá-la
                }
            }
        });
        form.setTitle(null);
        win.show();
    },

    /*#############
     Endereços
     #############*/
     onVincularEndereco: function(form) {
         // var form = this.getView().down('unidade-form');
         var combo = form.down('combobox[name=enderecoSearch]');
         var grid = form.down('grid[reference=enderecosGrid]');
         var store = grid.getStore();

         var enderecoRecord = combo.getSelection();

         if (!enderecoRecord) {
             Ext.Msg.alert('Aviso', 'Selecione um endereço primeiro');
             return;
         }

         // Verifica se já existe
         var exists = store.findExact('id', enderecoRecord.get('id')) >= 0;
         if (exists) {
           Ext.Msg.alert('Aviso', 'Este endereço já está vinculado');
            return;
         }

         // Desabilita temporariamente os controles
         //combo.setDisabled(true);
         //form.down('button[text=Vincular]').setDisabled(true);

         var unidadeId = form.getForm().findField('id').getValue();

         if (!unidadeId) {
             // Modo criação - adiciona localmente
             store.add(enderecoRecord.copy());
             combo.reset();
         } else {
             // Modo edição - faz requisição AJAX
             Ext.Ajax.request({
                 url: '/protected/api/unidades/' + unidadeId + '/enderecos/' + enderecoRecord.get('id'),
                 method: 'POST',
                 success: function() {
                     store.add(enderecoRecord.copy());
                     combo.reset();
                 },
                 failure: function() {
                    Ext.toast({
                        html: 'Falha ao vincular endereço',
                        timeout: 3000,
                        ui: 'error'
                    });
                 },
                 callback: function() {
                     // Reabilita os controles
                     combo.setDisabled(false);
                     form.down('button[text=Vincular]').setDisabled(false);
                     combo.focus();
                 }
             });
         }
     },


     onRemoverEndereco: function(form, record) {

         var grid = form.down('grid[reference=enderecosGrid]');
         var store = grid.getStore();
         // var record = store.getAt(rowIndex);
         // var form = grid.up('form');
         var unidadeId = form.getForm().findField('id').getValue();

         if (!unidadeId) {
             // Modo criação - remove localmente
             store.remove(record);
         } else {
             // Modo edição - faz requisição AJAX
             Ext.Ajax.request({
                 url: '/protected/api/unidades/' + unidadeId + '/enderecos/' + record.get('id'),
                 method: 'DELETE',
                 success: function() {
                     store.remove(record);
                     Ext.toast({
                         html: 'Endereço desvinculado com sucesso',
                         timeout: 3000
                     });
                 },
                 failure: function() {
                     Ext.toast({
                          html: 'Erro ao desvincular endereço',
                          timeout: 3000,
                          ui: 'error'
                      });
                 }
             });
         }
     },
     onSaveUnidade: function(btn) {
         var form = btn.up('form');
         var values = form.getValues();
         var enderecosGrid = form.down('grid[reference=enderecosGrid]');
         var enderecosRecords = enderecosGrid.getStore().getRange();

         // Prepara os dados no formato correto
         var dadosParaEnviar = {
             id: (!values.id ? null : values.id),
             nome: values.nome,
             sigla: values.sigla,
             enderecos: enderecosRecords.map(function(record) {
                 return record.get('id'); // Envia apenas os IDs dos endereços
             })
         };

         // Verifica se é uma nova unidade ou edição
         var url = '/protected/api/unidades';
         var method = 'POST';

         if (values.id) {
             url += '/' + values.id;
             method = 'PUT';
         }

         Ext.Ajax.request({
             url: url,
             method: method,
             jsonData: dadosParaEnviar,
             /*headers: {
                 'Content-Type': 'application/json',
                 'Accept': 'application/json'
             },*/
             success: function(response) {
                 Ext.getStore('Unidades').reload();
                 form.up('window').close();
                 Ext.toast({
                        html: 'Unidade cadastrada com sucesso!',
                        timeout: 3000
                    });
             },
             failure: function(response) {
                 var error = Ext.JSON.decode(response.responseText);
                 Ext.toast({
                        html: (error.message || 'Falha ao salvar unidade'),
                        timeout: 3000,
                        ui: 'error'
                    });
             }
         });
     },


     carregarEnderecosVinculados: function(view, unidadeId) {
        //console.log(pessoaId);
        //console.log(form);

         var grid = view.down('grid[reference=enderecosGrid]');
         var store = grid.getStore();

         Ext.Ajax.request({
             url: '/protected/api/unidades/' + unidadeId + '/enderecos',
             method: 'GET',
             success: function(response) {
                 var data = Ext.decode(response.responseText);
                 store.loadData(data);
             },
             failure: function() {
                Ext.toast({
                    html: 'Erro ao carregar endereços',
                    timeout: 3000,
                    ui: 'error'
                });
             }
         });
     }

});