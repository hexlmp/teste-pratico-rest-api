Ext.define('App.controller.PessoasController', {
    extend: 'Ext.app.Controller',
    alias: 'controller.pessoas',

    views: [
        'pessoas.PessoasGrid',
        'pessoas.PessoaForm'
    ],

    stores: [
        'Pessoas'
    ],

    refs: [{
        ref: 'pessoasGrid',
        selector: 'pessoas-grid'
    }],

    init: function() {
        this.control({
            'pessoas-grid': {
                onAddPessoa: this.onAddPessoa,
                onEditPessoa: this.onEditPessoa,
                onRemovePessoa: this.onRemovePessoa
            },
            'pessoa-form': {
                onSavePessoa: this.onSavePessoa,
                onCancelPessoa: this.onCancelPessoa
            }
        });
    },

    // Adicionar nova pessoa
    onAddPessoa: function() {
        var view = Ext.widget('pessoa-form', {
            // title: 'Adicionar Pessoa'
        });

        Ext.create('Ext.window.Window', {
            title: 'Nova Pessoa',
            width: 600,
            height: 600,
            layout: 'fit',
            modal: true,
            items: view,
            listeners: {
                close: function(win) {
                    win.destroy(); // Destrói a janela ao fechá-la
                }
            }
        }).show();
    },

    // Editar pessoa existente
    onEditPessoa: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];
        var view = Ext.widget('pessoa-form', {
            //title: 'Editar Pessoa'
        });

        var win = Ext.create('Ext.window.Window', {
            title: 'Editar Pessoa - ' + record.get('nome'),
            width: 600,
            height: 600,
            layout: 'fit',
            modal: true,
            items: view,
            listeners: {
                close: function(win) {
                    win.destroy(); // Destrói a janela ao fechá-la
                }
            }
        });

        view.loadRecord(record);
        win.show();

        // Carrega as fotos existentes da pessoa
        this.carregarFotosExistente(record.get('id'), view, 'thumbnailsContainer');

        // Carrega os endereços vinculados
        this.carregarEnderecosVinculados(record.get('id'), view);
    },

    // Remover pessoa
    onRemovePessoa: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];

        Ext.Msg.confirm('Confirmação', 'Deseja realmente remover ' + record.get('nome') + '?', function(btn) {
            if (btn === 'yes') {
                grid.getStore().remove(record);
                grid.getStore().sync({
                    success: function() {
                        Ext.toast({
                            html: 'Pessoa removida com sucesso',
                            timeout: 3000
                        });
                    },
                    failure: function() {
                        Ext.toast({
                            html: 'Erro ao remover pessoa',
                            timeout: 3000,
                            ui: 'error'
                        });
                    }
                });
            }
        });
    },

    // Salvar pessoa
    onSavePessoa: function(button) {
        var form = button.up('form');
        var values = form.getValues();
        var record = form.getRecord();
        var isNew = !record;
        // Endereços
        var enderecosGrid = form.down('grid[reference=enderecosGrid]');
        var enderecosRecords = enderecosGrid.getStore().getRange();
        values.enderecos = enderecosRecords.map(
                                        function(record) {
                                            return record.get('id'); // Envia apenas os IDs dos endereços
                                        });

        // Coletar IDs temporários das fotos
        var thumbnails = form.down('container[reference=thumbnailsContainer]').items.items;
        var tempFotoIds = Ext.Array.map(thumbnails, function(thumb) {
            return thumb.serverTempId;
        });

        // Adicionar ao objeto que será enviado
        values.tempFotoIds = tempFotoIds;

        // Confirmação antes de salvar
        Ext.Msg.confirm('Confirmação', 'Deseja realmente ' + (isNew ? 'cadastrar' : 'atualizar') + ' esta pessoa?', function(btn) {
            if (btn === 'yes') {
                // Configura a requisição
                var url = '/protected/api/pessoas';
                var method = isNew ? 'POST' : 'PUT';

                // Para atualização, inclui o ID
                if (!isNew) {
                    url += '/' + values.id;
                }

                // Envia a requisição
                Ext.Ajax.request({
                    url: url,
                    method: method,
                    jsonData: values, // Envia como JSON no body
                    success: function(response) {
                        var data = Ext.decode(response.responseText);

                        // Atualiza a grid
                        var grid = Ext.ComponentQuery.query('pessoas-grid')[0];
                        grid.getStore().load();

                        // Fecha a janela
                        form.up('window').close();

                        // Feedback para o usuário
                        Ext.toast({
                            html: 'Pessoa ' + (isNew ? 'cadastrada' : 'atualizada') + ' com sucesso',
                            timeout: 3000
                        });

                        // Se for novo, seleciona o registro criado
                        if (isNew && data.id) {
                            var newRecord = grid.getStore().getById(data.id);
                            if (newRecord) {
                                grid.getSelectionModel().select(newRecord);
                            }
                        }
                    },
                    failure: function(response) {
                        var errorMsg = 'Erro ao ' + (isNew ? 'cadastrar' : 'atualizar') + ' pessoa';

                        try {
                            var errorResponse = Ext.decode(response.responseText);
                            if (errorResponse.message) {
                                errorMsg += ': ' + errorResponse.message;
                            }
                        } catch (e) {
                            // Não conseguiu parsear a resposta de erro
                        }

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

    // Cancelar edição
    onCancelPessoa: function(button) {
        button.up('window').close();
    },

    /*  ############
        Fotos Min.IO
        ############*/
    onFotosSelecionadas: function(field, files) {
        var form = field.up('form');
        var thumbnailsContainer = form.down('container[reference=thumbnailsContainer]');

        // Limpar mensagens de erro anteriores
        field.clearInvalid();

        // Validar arquivos
        for (var i = 0; i < files.length; i++) {
            if (!files[i].type.match('image.*')) {
                field.markInvalid('Apenas arquivos de imagem são permitidos');
                return;
            }

            if (files[i].size > 5 * 1024 * 1024) { // 5MB
                field.markInvalid('Arquivos não podem ser maiores que 5MB');
                return;
            }
        }

        // Processar cada arquivo
        Ext.Array.each(files, function(file) {
            this.processarFoto(file, thumbnailsContainer);
        }, this);

        // Resetar o campo para permitir novas seleções
        field.reset();
    },

    processarFoto: function(file, thumbnailsContainer) {
        var reader = new FileReader();
        var tempId = 'temp-' + Ext.id();

        reader.onload = function(e) {
            var thumbnail = thumbnailsContainer.add(
            {
                xtype: 'container',
                width: 115,
                height: 160,
                cls: 'thumbnail-item',
                layout: 'vbox',
                /*style: {
                    border: '1px solid #ddd',
                    borderRadius: '4px',
                    padding: '5px'
                },*/
                items: [{
                    xtype: 'image',
                    width: 100,
                    height: 100,
                    src: e.target.result,
                    style: {
                        objectFit: 'cover'
                    }
                }, {
                    xtype: 'container',
                    html: file.name,
                    width: 100,
                    height: 20,
                    style: {
                        whiteSpace: 'nowrap',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        fontSize: '10px',
                        textAlign: 'center'
                    }
                }, {
                    xtype: 'button',
                    text: 'Remover',
                    iconCls: 'x-fa fa-trash',
                    width: 100,
                    height: 30,
                    scale: 'small',
                    handler: function() {
                        this.removerFotoTemp(thumbnail, tempId);
                    },
                    scope: this
                }],
                fileData: file,
                tempId: tempId
            });

            // Fazer upload para o servidor
            this.fazerUploadTemporario(file, tempId, thumbnail);
        }.bind(this);

        reader.readAsDataURL(file);
    },

    fazerUploadTemporario: function(file, tempId, thumbnail) {
        var formData = new FormData();
        formData.append('file', file);

        Ext.Ajax.request({
            url: '/protected/api/pessoas/temp-upload',
            method: 'POST',
            rawData: formData,
            headers: {
                'Content-Type': undefined
            },
            success: function(response) {
                var data = Ext.decode(response.responseText);
                thumbnail.serverTempId = data.tempId;
                thumbnail.setLoading(false);
            },
            failure: function() {
                thumbnail.setLoading(false);
                Ext.toast('Erro ao enviar foto', 'Erro', 'error');
            },
            scope: this
        });

        thumbnail.setLoading('Enviando...');
    },

    removerFotoTemp: function(thumbnail, tempId) {
        Ext.Msg.confirm('Confirmação', 'Deseja realmente remover esta foto?', function(btn) {
            if (btn === 'yes') {
                if (thumbnail.serverTempId) {
                    Ext.Ajax.request({
                        url: '/protected/api/pessoas/temp-upload/' + thumbnail.serverTempId,
                        method: 'DELETE'
                    });
                }
                thumbnail.destroy();
            }
        });
    },

    findThumbnailByTempId: function(tempId) {
        var thumbnails = Ext.ComponentQuery.query('pessoa-form container[reference=thumbnailsContainer] > container');
        return Ext.Array.findBy(thumbnails, function(thumb) {
            return thumb.tempId === tempId;
        });
    },
    carregarFotosExistente: function(pessoaId, form, containerReference) {
        if (!containerReference) containerReference = 'thumbnailsContainer';
        Ext.Ajax.request({
            url: '/protected/api/pessoas/' + pessoaId + '/fotos',
            method: 'GET',
            success: function(response) {
                var fotos = Ext.decode(response.responseText);
                var container = form.down('container[reference='+containerReference+']');

                // Limpa thumbnails existentes
                container.removeAll();

                // Adiciona cada foto ao container
                Ext.Array.each(fotos, function(foto) {
                    this.adicionarThumbnailExistente(container, foto);
                }, this);
            },
            failure: function() {
                Ext.toast('Erro ao carregar fotos', 'Erro', 'error');
            },
            scope: this
        });
    },

    adicionarThumbnailExistente: function(container, foto) {
        container.add({
            xtype: 'container',
            width: 115,
            height: 160,
            layout: 'vbox',
            style: {
                border: '1px solid #ddd',
                borderRadius: '4px',
                padding: '5px'
            },
            items: [{
                xtype: 'image',
                width: 100,
                height: 100,
                src: foto.url,
                style: {
                    objectFit: 'cover'
                }
            }, {
                xtype: 'container',
                html: foto.id,
                width: 100,
                height: 20,
                style: {
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    fontSize: '10px',
                    textAlign: 'center'
                }
            }, {
                xtype: 'button',
                text: 'Remover',
                iconCls: 'x-fa fa-trash',
                width: 100,
                height: 30,
                scale: 'small',
                handler: function() {
                    this.removerFotoDefinitiva(foto.id, container);
                },
                scope: this
            }],
            fotoId: foto.id
        });
    },

    removerFotoDefinitiva: function(fotoId, container) {
        Ext.Msg.confirm('Confirmação', 'Deseja realmente remover esta foto?', function(btn) {
            if (btn === 'yes') {
                Ext.Ajax.request({
                    url: '/protected/api/pessoas/fotos/' + fotoId,
                    method: 'DELETE',
                    success: function() {
                        // Remove o thumbnail do container
                        var thumbnail = container.items.findBy(function(item) {
                            return item.fotoId === fotoId;
                        });
                        if (thumbnail) {
                            container.remove(thumbnail);
                        }
                        Ext.toast('Foto removida com sucesso', 'Sucesso');
                    },
                    failure: function() {
                        Ext.toast('Erro ao remover foto', 'Erro', 'error');
                    }
                });
            }
        });
    },

     /*#############
     Endereços
     #############*/
     onVincularEndereco: function(form, enderecoRecord) {
         var pessoaId = form.getForm().findField('id').getValue();
         var grid = form.down('grid[reference=enderecosGrid]');
         var store = grid.getStore();

         // Verifica se o endereço já está vinculado
         var alreadyExists = store.findExact('id', enderecoRecord.get('id')) >= 0;

         if (alreadyExists) {
             Ext.Msg.alert('Aviso', 'Este endereço já está vinculado à pessoa');
             return;
         }

         if (!pessoaId) {
              // Modo criação - adiciona localmente
              store.add(enderecoRecord.copy());

          } else {

             Ext.Ajax.request({
                 url: '/protected/api/pessoas/' + pessoaId + '/enderecos/' + enderecoRecord.get('id'),
                 method: 'POST',
                 success: function() {
                     // Adiciona o endereço ao grid localmente
                     store.add(enderecoRecord);
                     Ext.toast('Endereço vinculado com sucesso', 'Sucesso');
                 },
                 failure: function(response) {
                     Ext.toast('Erro ao vincular endereço', 'Erro', 'error');
                 }
             });
         }
     },

     onDesvincularEndereco: function(form, enderecoRecord) {
         var pessoaId = form.getForm().findField('id').getValue();
         var grid = form.down('grid[reference=enderecosGrid]');
         var store = grid.getStore();

         if (!pessoaId) {
              // Modo criação - remove localmente
              store.remove(enderecoRecord);

          } else {

             Ext.Msg.confirm('Confirmação', 'Deseja realmente desvincular este endereço?', function(btn) {
                 if (btn === 'yes') {
                     Ext.Ajax.request({
                         url: '/protected/api/pessoas/' + pessoaId + '/enderecos/' + enderecoRecord.get('id'),
                         method: 'DELETE',
                         success: function() {
                             store.remove(enderecoRecord);
                             Ext.toast('Endereço desvinculado com sucesso', 'Sucesso');
                         },
                         failure: function() {
                             Ext.toast('Erro ao desvincular endereço', 'Erro', 'error');
                         }
                     });
                 }
             });
          }
     },

     carregarEnderecosVinculados: function(pessoaId, form, containerReference) {
        //console.log(pessoaId);
        //console.log(form);

        if (!containerReference) containerReference = 'enderecosGrid';


         var grid = form.down('grid[reference='+containerReference+']');
         var store = grid.getStore();

         Ext.Ajax.request({
             url: '/protected/api/pessoas/' + pessoaId + '/enderecos',
             method: 'GET',
             success: function(response) {
                 var data = Ext.decode(response.responseText);
                 store.loadData(data);
             },
             failure: function() {
                 Ext.toast('Erro ao carregar endereços', 'Erro', 'error');
             }
         });
     }
});