Ext.define('App.view.servidores.ServidoresEfetivosGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'servidores-efetivos-grid',

    requires: [
        'Ext.grid.column.Column',
        'Ext.toolbar.Paging',
        'App.store.ServidoresEfetivos'
    ],

    title: 'CRUD - Servidores Efetivos',
    store: 'ServidoresEfetivos',

    columns: [
        {
            text: 'Matrícula',
            dataIndex: 'matricula',
            width: 200
        },
        {
            text: 'ID Pessoa',
            dataIndex: 'pessoa',
            renderer: function(value) {
                //console.log(value);
                return value && value.id ? value.id : 'Não informado';
            },
            width: 150
        },
        {
            text: 'Nome',
            dataIndex: 'pessoa',
            renderer: function(value) {
                return value && value.nome ? value.nome : 'Não informado';
            },
            flex: 2
        },
        {
            text: 'Nascimento',
            dataIndex: 'pessoa',
            //xtype: 'datecolumn',
            //format: 'd/m/Y',
            renderer: function(value) {
                if (!value || !value.dataNascimento) {
                    return 'Não informado';
                }

                // Converte a string de data para um objeto Date, se necessário
                const date = Ext.Date.parse(value.dataNascimento, 'Y-m-d');

                // Formata a data no padrão brasileiro
                return Ext.Date.format(date, 'd/m/Y');
            },
            width: 120
        },
        {
            text: 'Sexo',
            dataIndex: 'pessoa',
            renderer: function(value) {
                return value && value.sexo ? value.sexo : 'Não informado';
            },
            width: 80
        },
        /*,
        {
            text: 'Ações',
            xtype: 'actioncolumn',
            width: 100,
            items: [{
                iconCls: 'x-fa fa-edit',
                tooltip: 'Editar',
                handler: function(grid, rowIndex) {
                    Ext.getApplication().getController('ServidoresController').onEditServidorEfetivo(grid);
                }
            }, {
                iconCls: 'x-fa fa-trash',
                tooltip: 'Remover',
                handler: function(grid, rowIndex) {
                    Ext.getApplication().getController('ServidoresController').onRemoveServidorEfetivo(grid);
                }
            }]
        }*/
    ],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            text: 'Adicionar',
            iconCls: 'x-fa fa-plus',
            action: 'add',
            handler: function() {
                Ext.getApplication().getController('ServidoresController').onAddServidorEfetivo();
            }
        }, {
            text: 'Editar',
            iconCls: 'x-fa fa-edit',
            action: 'edit',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid');
                Ext.getApplication().getController('ServidoresController').onEditServidorEfetivo(grid);
            },
            bind: {
                disabled: '{!servidoresEfetivosGrid.selection}'
            }
        }, {
            text: 'Remover',
            iconCls: 'x-fa fa-trash',
            action: 'remove',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid');
                Ext.getApplication().getController('ServidoresController').onRemoveServidorEfetivo(grid);
            },
            bind: {
                disabled: '{!servidoresEfetivosGrid.selection}'
            }
        }, '->', {
            xtype: 'textfield',
            emptyText: 'Pesquisar por matrícula...',
            width: 250,
            listeners: {
                change: {
                    fn: function(field) {
                        var grid = field.up('grid');
                        var store = grid.getStore();
                        store.clearFilter();

                        if (field.getValue()) {
                            store.filter([{
                                property: 'matricula',
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
        store: 'ServidoresEfetivos',
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
            // Chama o método de edição do controller
            Ext.getApplication().getController('ServidoresController').onEditServidorEfetivo(grid);
        }
    }
});