Ext.define('App.view.unidades.UnidadeForm', {
    extend: 'Ext.form.Panel',
    xtype: 'unidade-form',

    bodyPadding: 20,
    defaults: {
        anchor: '100%',
        labelWidth: 120,
        msgTarget: 'side'
    },

    items: [{
        xtype: 'hiddenfield',
        name: 'id'
    }, {
        xtype: 'textfield',
        name: 'nome',
        fieldLabel: 'Nome',
        allowBlank: false,
        maxLength: 100,
        enforceMaxLength: true
    }, {
        xtype: 'textfield',
        name: 'sigla',
        fieldLabel: 'Sigla',
        allowBlank: false,
        maxLength: 10,
        enforceMaxLength: true
    },
    {
        xtype: 'container',
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        //flex: 1,
        // margin: '10 0 0 0', // Adiciona 10px de margem superior
        // padding: '10 0 0 0', // Adiciona 10px de espaço interno no topo
        items: [
            {
                xtype: 'container',
                layout: 'hbox',
                items: [
                {
                    xtype: 'combobox',
                    name: 'enderecoSearch',
                    fieldLabel: 'Endereço',
                    labelWidth: 120,
                    flex: 1,
                    margin: '0 0 10 0',
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
                            // console.log(record);
                            Ext.getApplication().getController('UnidadesController').onVincularEndereco(form, record);
                        } else {
                            Ext.Msg.alert('Aviso', 'Selecione um endereço para vincular');
                        }
                    }
                }]
            }, {
                xtype: 'grid',
                title: 'Endereços Vinculados',
                reference: 'enderecosGrid',
                height: 200,
                scrollable: true,
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
                            console.log(form);
                            console.log(record);
                            Ext.getApplication().getController('UnidadesController').onRemoverEndereco(form, record);
                        }
                    }]
                }]
            }
        ]
    }

    ],

    buttons: [{
        text: 'Salvar',
        formBind: true,
        iconCls: 'x-fa fa-save',
        handler: function(btn) {
            Ext.getApplication().getController('UnidadesController').onSaveUnidade(btn);
        }
    }, {
        text: 'Cancelar',
        iconCls: 'x-fa fa-times',
        handler: function(btn) {
            Ext.getApplication().getController('UnidadesController').onCancelUnidade(btn);
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