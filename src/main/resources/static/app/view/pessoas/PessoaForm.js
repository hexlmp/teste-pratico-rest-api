Ext.define('App.view.pessoas.PessoaForm', {
    extend: 'Ext.form.Panel',
    xtype: 'pessoa-form',

    bodyPadding: 0,
    defaults: {
        anchor: '100%',
        labelWidth: 120,
        msgTarget: 'side'
    },

    items: [
        {
            xtype: 'hiddenfield',
            name: 'id'
        },
        {
        xtype: 'tabpanel',
        plain: false,
        defaults: {
            bodyPadding: 10,
            anchor: '100%'
        },
        items: [
            {
            title: 'Dados Pessoais',
            iconCls: 'x-fa fa-id-card',
            layout: 'anchor',
            defaults: {
                bodyPadding: 10,
                anchor: '100%',
                labelWidth: 120,
                msgTarget: 'side'
            },
            items: [{
                xtype: 'textfield',
                name: 'nome',
                fieldLabel: 'Nome Completo',
                allowBlank: false,
                maxLength: 100,
                enforceMaxLength: true
            }, {
                xtype: 'datefield',
                name: 'dataNascimento',
                fieldLabel: 'Data Nascimento',
                format: 'd/m/Y',
                submitFormat: 'Y-m-d',
                allowBlank: false,
                maxValue: new Date()
            }, {
                xtype: 'combobox',
                name: 'sexo',
                fieldLabel: 'Sexo',
                store: ['Masculino', 'Feminino', 'Outro'],
                editable: false,
                allowBlank: false
            }, {
                xtype: 'textfield',
                name: 'mae',
                fieldLabel: 'Nome da Mãe',
                maxLength: 100,
                enforceMaxLength: true
            }, {
                xtype: 'textfield',
                name: 'pai',
                fieldLabel: 'Nome do Pai',
                maxLength: 100,
                enforceMaxLength: true
            }]
        }, {
            title: 'Fotos',
            iconCls: 'x-fa fa-camera',
            defaults: {
                bodyPadding: 10,
                anchor: '100%',
                labelWidth: 120,
                msgTarget: 'side'
            },
            items: [{
                xtype: 'fileuploadfield',
                // xtype: 'filefield',
                name: 'fotos',
                fieldLabel: 'Fotos',
                buttonText: 'Selecionar...',
                anchor: '100%',
                width: '100%',
                buttonConfig: {
                    //iconCls: 'x-fa fa-image'
                    iconCls: 'x-fa fa-camera'
                },
                listeners: {
                    change: function(field, value) {
                        if (value) {
                            var files = field.fileInputEl.dom.files;
                            Ext.getApplication().getController('PessoasController').onFotosSelecionadas(field, files);
                        }
                    }
                },
                multiple: true,
                accept: 'image/*',
                componentCls: 'custom-file-upload',
                validator: function(value) {
                    if (value) {
                        var files = this.fileInputEl.dom.files;
                        for (var i = 0; i < files.length; i++) {
                            if (!files[i].type.match('image.*')) {
                                return 'Apenas arquivos de imagem são permitidos';
                            }
                        }
                    }
                    return true;
                }
            }, {
                xtype: 'container',
                reference: 'thumbnailsContainer',
                layout: {
                    type: 'table',
                    columns: 4,
                    tdAttrs: {
                        style: 'padding: 5px; vertical-align: top;'
                    }
                },
                /*layout: {
                    type: 'hbox',
                    pack: 'start'
                    //overflowHandler: 'scroller'
                },*/
                scrollable: true,
                //cls: 'thumbnail-grid-container', // Classe principal
                height: 400,
                defaults: {
                    margin: '0 5 5 0'
                },
                items: []
            }]
        },
        {
            title: 'Endereços',
            iconCls: 'x-fa fa-map-marker-alt',
            items: [
                {
                    xtype: 'container',
                    layout: 'hbox',
                    margin: '0 0 10 0',
                    items: [
                    {
                        xtype: 'combobox',
                        name: 'enderecoSearch',
                        fieldLabel: 'Endereço',
                        labelWidth: 100,
                        flex: 1,
                        margin: '0 0 0 0',
                        displayField: 'logradouroCompleto',
                        valueField: 'id',
                        queryMode: 'remote',
                        minChars: 3,
                        typeAhead: true,
                        store: {
                            autoLoad: false,
                            fields: ['id', 'logradouro', 'numero', 'bairro', 'cidade', 'logradouroCompleto'],
                            proxy: {
                                type: 'rest',
                                url: '/protected/api/enderecos/search',
                                reader: {
                                    type: 'json',
                                    rootProperty: 'data'
                                }
                            }
                        },
                        tpl: Ext.create('Ext.XTemplate',
                            '<tpl for=".">',
                            '<div class="x-boundlist-item">',
                            '<div><strong>{logradouro}, {numero}</strong> - {bairro}</div>',
                            '<div style="font-size: smaller; color: #666;">{cidade.nome}/{cidade.uf}</div>',
                            '</div>',
                            '</tpl>'
                        ),
                        displayTpl: Ext.create('Ext.XTemplate',
                            '<tpl for=".">',
                            '{logradouroCompleto}',
                            '</tpl>'
                        ),
                        listeners: {
                            beforequery: function(queryPlan) {
                                queryPlan.query = queryPlan.query.trim();
                            }
                        }
                    }, {
                        xtype: 'button',
                        text: 'Vincular',
                        iconCls: 'x-fa fa-link',
                        handler: function(btn) {
                            var form = btn.up('form');
                            var combo = form.down('combobox[name=enderecoSearch]');
                            var record = combo.getSelection();

                            if (record) {
                                Ext.getApplication().getController('PessoasController')
                                    .onVincularEndereco(form, record);
                            } else {
                                Ext.Msg.alert('Aviso', 'Selecione um endereço para vincular');
                            }
                        }
                    }]
                }, {
                    xtype: 'grid',
                    title: 'Endereços Vinculados',
                    reference: 'enderecosGrid',
                    flex: 1,
                    store: {
                        autoLoad: false,
                        fields: ['id', 'tipoLogradouro', 'logradouro', 'numero', 'bairro', 'cidade'],
                        data: []
                    },
                    columns: [{
                        text: 'Logradouro',
                        dataIndex: 'logradouro',
                        flex: 2,
                        renderer: function(value, meta, record) {
                            var tipo = record.get('tipoLogradouro') || '';
                            return tipo + ' ' + value;
                        }
                    }, {
                        text: 'Número',
                        dataIndex: 'numero',
                        width: 80
                    }, {
                        text: 'Bairro',
                        dataIndex: 'bairro',
                        flex: 1
                    }, {
                        text: 'Cidade',
                        dataIndex: 'cidade',
                        flex: 1,
                        renderer: function(value) {
                            return value ? value.nome + '/' + value.uf : '';
                        }
                    }, {
                        xtype: 'actioncolumn',
                        width: 50,
                        items: [{
                            iconCls: 'x-fa fa-unlink',
                            tooltip: 'Desvincular endereço',
                            handler: function(grid, rowIndex, colIndex) {
                                var form = grid.up('form');
                                var record = grid.getStore().getAt(rowIndex);
                                Ext.getApplication().getController('PessoasController')
                                    .onDesvincularEndereco(form, record);
                            }
                        }]
                    }]
                }
            ]
        }]
    }],

    cls: 'pessoa-form-with-uploads',

    buttons: [{
        text: 'Salvar',
        formBind: true,
        iconCls: 'x-fa fa-save',
        handler: function(btn) {
            Ext.getApplication().getController('PessoasController').onSavePessoa(btn);
        }
    }, {
        text: 'Cancelar',
        iconCls: 'x-fa fa-times',
        handler: function(btn) {
            Ext.getApplication().getController('PessoasController').onCancelPessoa(btn);
        }
    }],

    beforeSave: function() {
        var form = this;

        if (!form.isValid()) {
            Ext.Msg.alert('Erro', 'Por favor, corrija os campos inválidos.');
            return false;
        }

        return true;
    }
});