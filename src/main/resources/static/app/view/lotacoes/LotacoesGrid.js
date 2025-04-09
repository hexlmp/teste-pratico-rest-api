Ext.define('App.view.lotacoes.LotacoesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'lotacoes-grid',

    requires: [
        'App.store.Lotacoes',
        'Ext.grid.column.Column',
        'Ext.toolbar.Paging'
    ],

    title: 'CRUD - Lotações',
    store: 'Lotacoes',

    columns: [
        {
            text: 'ID',
            dataIndex: 'id',
            width: 80
        },
        {
            text: 'Pessoa',
            dataIndex: 'pessoa',
            flex: 2,
            renderer: function(value) {
                return value && value.nome ? value.nome : 'Não informado';
            }
        },
        {
            text: 'Unidade',
            dataIndex: 'unidade',
            flex: 2,
            renderer: function(value) {
                return value && value.nome ? value.nome : 'Não informado';
            }
        },
        {
            text: 'Data Lotação',
            dataIndex: 'dataLotacao',
            xtype: 'datecolumn',
            format: 'd/m/Y',
            width: 120
        },
        {
            text: 'Data Remoção',
            dataIndex: 'dataRemocao',
            xtype: 'datecolumn',
            format: 'd/m/Y',
            width: 120
        },
        {
            text: 'Portaria',
            dataIndex: 'portaria',
            flex: 1
        }
    ],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [
        {
            text: 'Adicionar',
            iconCls: 'x-fa fa-plus',
            handler: function() {
                 Ext.getApplication().getController('LotacoesController').onAddLotacao();
            }
        }, {
            text: 'Editar',
            iconCls: 'x-fa fa-edit',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid');
                Ext.getApplication().getController('LotacoesController').onEditLotacao(grid);
            },
            bind: {
                disabled: '{!lotacoesGrid.selection}'
            }
        }, {
            text: 'Remover',
            iconCls: 'x-fa fa-trash',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid');
                Ext.getApplication().getController('LotacoesController').onRemoveLotacao(grid);
            },
            bind: {
                disabled: '{!lotacoesGrid.selection}'
            }
        }, '->', {
            xtype: 'textfield',
            emptyText: 'Pesquisar...',
            width: 250,
            listeners: {
                change: {
                    fn: function(field) {
                        var grid = field.up('grid');
                        var store = grid.getStore();
                        store.clearFilter();

                        if (field.getValue()) {
                            store.filter([{
                                property: 'portaria',
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
        store: 'Lotacoes',
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
        itemdblclick: 'onEditLotacao'
    }
});