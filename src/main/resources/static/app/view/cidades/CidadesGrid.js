Ext.define('App.view.cidades.CidadesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'cidades-grid',

    requires: [
        'Ext.grid.column.Column',
        'Ext.toolbar.Paging',
        'App.store.Cidades',
        'Ext.grid.feature.Grouping'
    ],

    title: 'CRUD - Cidades',
    store: {
        type: 'cidades',
        groupField: 'uf'  // Define o campo para agrupamento
    },

    features: [{
        ftype: 'grouping',
        groupHeaderTpl: 'Unidade Federativa: {name} ({rows.length} {[values.rows.length > 1 ? "Cidades" : "Cidade"]})',
        hideGroupedHeader: false,
        enableGroupingMenu: true,
        startCollapsed: false
    }],

    columns: [
        {
            text: 'ID',
            dataIndex: 'id',
            width: 50
        },
        {
            text: 'Nome',
            dataIndex: 'nome',
            flex: 2
        },
        {
            text: 'UF',
            dataIndex: 'uf',
            width: 80,
            hidden: true  // Opcional - oculta a coluna UF pois já aparece no agrupamento
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
                Ext.getApplication().getController('CidadesController').onAddCidade();
            }
        }, {
            text: 'Editar',
            iconCls: 'x-fa fa-edit',
            action: 'edit',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid');
                Ext.getApplication().getController('CidadesController').onEditCidade(grid);
            },
            bind: {
                disabled: '{!cidadesGrid.selection}'
            }
        }, {
            text: 'Remover',
            iconCls: 'x-fa fa-trash',
            action: 'remove',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid');
                Ext.getApplication().getController('CidadesController').onRemoveCidade(grid);
            },
            bind: {
                disabled: '{!cidadesGrid.selection}'
            }
        }, '->', {
            xtype: 'textfield',
            emptyText: 'Pesquisar por nome...',
            width: 250,
            listeners: {
                change: {
                    fn: function(field) {
                        var grid = field.up('grid');
                        var store = grid.getStore();
                        store.clearFilter();

                        if (field.getValue()) {
                            store.filter([{
                                property: 'nome',
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
        store: 'Cidades',
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
            Ext.getApplication().getController('CidadesController').onEditCidade(grid);
        }
    }
});