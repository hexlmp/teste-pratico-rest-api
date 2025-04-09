Ext.define('App.view.servidores.ServidoresTemporariosGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'servidores-temporarios-grid',

    requires: [
        'Ext.grid.column.Date',
        'Ext.toolbar.Paging',
        'App.store.ServidoresTemporarios'
    ],

    title: 'CRUD - Servidores Temporários',
    store: 'ServidoresTemporarios',

    columns: [
        {
            text: 'Nome',
            dataIndex: 'pessoa',
            renderer: function(value) {
                return value && value.nome ? value.nome : 'Não informado';
            },
            flex: 2
        },
        {
            text: 'Data Admissão',
            dataIndex: 'dataAdmissao',
            xtype: 'datecolumn',
            format: 'd/m/Y',
            flex: 1
        },
        {
            text: 'Data Demissão',
            dataIndex: 'dataDemissao',
            xtype: 'datecolumn',
            format: 'd/m/Y',
            flex: 1
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
                 Ext.getApplication().getController('ServidoresController').onAddServidorTemporario();
             }
        }, {
            text: 'Editar',
            iconCls: 'x-fa fa-edit',
            action: 'edit',
            disabled: true,
            handler: function(btn) {
                 var grid = btn.up('grid');
                 Ext.getApplication().getController('ServidoresController').onEditServidorTemporario(grid);
             },
            bind: {
                disabled: '{!servidoresTemporariosGrid.selection}'
            }
        }, {
            text: 'Remover',
            iconCls: 'x-fa fa-trash',
            action: 'remove',
            disabled: true,
            handler: function(btn) {
                     var grid = btn.up('grid');
                     Ext.getApplication().getController('ServidoresController').onRemoveServidorTemporario(grid);
                 },
            bind: {
                disabled: '{!servidoresTemporariosGrid.selection}'
            }
        }, '->', {
             // Campo de filtro por nome
             xtype: 'textfield',
             emptyText: 'Pesquisar por nome...',
             width: 250,
             listeners: {
                 change: {
                     fn: function(field) {
                         var grid = field.up('grid');
                         var store = grid.getStore();
                         var value = field.getValue().toLowerCase();

                         store.clearFilter();

                         if (value) {
                             store.filterBy(function(record) {
                                 var pessoa = record.get('pessoa');
                                 var nome = pessoa && pessoa.nome ? pessoa.nome.toLowerCase() : '';
                                 return nome.includes(value);
                             });
                         }
                     },
                     buffer: 300 // Delay de 300ms para evitar muitas requisições
                 }
             }
         }
        ]
    }, {
        xtype: 'pagingtoolbar',
        dock: 'bottom',
        store: 'ServidoresTemporarios',
        displayInfo: true
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
            Ext.getApplication().getController('ServidoresController').onEditServidorTemporario(grid);
        }
    }
});