Ext.define('App.view.enderecos.EnderecosGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'enderecos-grid',

    requires: [
        'Ext.grid.column.Column',
        'Ext.toolbar.Paging',
        'App.store.Enderecos',
        'App.store.Cidades'
    ],

    title: 'CRUD - Endereços',
    store: 'Enderecos',

    columns: [
        { text: 'ID', dataIndex: 'id', width: 50 },
        { text: 'Tipo', dataIndex: 'tipoLogradouro', width: 100 },
        {
            text: 'Logradouro',
            dataIndex: 'logradouro',
            flex: 2
        },
        /*{
            text: 'Logradouro',
            dataIndex: 'logradouro',
            flex: 2,
            renderer: function(value, meta, record) {
                var tipo = record.get('tipoLogradouro') || '';
                return tipo + ' ' + value;
            }
        },*/
        { text: 'Número', dataIndex: 'numero', width: 80 },
        { text: 'Bairro', dataIndex: 'bairro', flex: 1 },
        {
            text: 'Cidade',
            dataIndex: 'cidade',
            flex: 1,
            renderer: function(value) {
                return value ? value.nome + '/' + value.uf : '';
            }
        }
    ],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            text: 'Adicionar',
            iconCls: 'x-fa fa-plus',
            action: 'add',
            handler: function() {
                Ext.getApplication().getController('EnderecosController').onAddEndereco();
            }
        }, {
            text: 'Editar',
            iconCls: 'x-fa fa-edit',
            action: 'edit',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid');
                Ext.getApplication().getController('EnderecosController').onEditEndereco(grid);
            },
            bind: {
                disabled: '{!enderecosGrid.selection}'
            }
        }, {
            text: 'Remover',
            iconCls: 'x-fa fa-trash',
            action: 'remove',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid');
                Ext.getApplication().getController('EnderecosController').onRemoveEndereco(grid);
            },
            bind: {
                disabled: '{!enderecosGrid.selection}'
            }
        }, '->', {
            xtype: 'textfield',
            emptyText: 'Pesquisar por logradouro...',
            width: 250,
            listeners: {
                change: {
                    fn: function(field) {
                        var grid = field.up('grid');
                        var store = grid.getStore();
                        store.clearFilter();

                        if (field.getValue()) {
                            store.filter([{
                                property: 'logradouro',
                                value: field.getValue(),
                                anyMatch: true,
                                caseSensitive: false
                            }]);
                        }
                    },
                    buffer: 300
                }
            }
        }]
    }, {
        xtype: 'pagingtoolbar',
        dock: 'bottom',
        store: 'Enderecos',
        displayInfo: true,
        displayMsg: 'Mostrando {0} - {1} de {2}',
        emptyMsg: 'Nenhum registro encontrado'
    }],

    selModel: {
        selType: 'rowmodel',
        mode: 'SINGLE'
    },

    listeners: {
        selectionchange: function(selModel, selected) {
            var actions = this.down('toolbar').query('[action]');
            Ext.each(actions, function(action) {
                if (action.action !== 'add') {
                    action.setDisabled(selected.length === 0);
                }
            });
        },
        itemdblclick: function(grid, record, item, index, e) {
            Ext.getApplication().getController('EnderecosController').onEditEndereco(grid);
        }
    }
});