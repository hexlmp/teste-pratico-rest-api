Ext.define('App.view.unidades.UnidadesGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'unidades-grid',

    requires: [
        'Ext.grid.column.Column',
        'Ext.toolbar.Paging',
        'App.store.Unidades'
    ],

    title: 'CRUD - Unidades',
    store: 'Unidades',

    columns: [
        { text: 'ID', dataIndex: 'id', width: 50 },
        { text: 'Nome', dataIndex: 'nome', flex: 2 },
        { text: 'Sigla', dataIndex: 'sigla', width: 100 },
        {
            text: 'Endereços',
            dataIndex: 'enderecos',
            flex: 2,
            renderer: function(value) {
                if (Array.isArray(value) && value.length) {
                    return `
                        <div class="enderecos-container">
                            <ul class="enderecos-lista">
                                ${value.map(endereco => `
                                    <li class="endereco-item">
                                        ${endereco.logradouro}, ${endereco.numero}, ${endereco.cidade.nome}/${endereco.cidade.uf}
                                    </li>
                                `).join('')}
                            </ul>
                        </div>
                    `;
                }
                return '<div class="sem-endereco">Nenhum endereço cadastrado</div>';
            }
            /*renderer: function(value) {
                if (value && value.length > 0) {
                    return value.map(function(endereco) {
                        return endereco.logradouro + ', ' + endereco.numero;
                    }).join('; ');
                }
                return 'Nenhum endereço cadastrado';
            }*/
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
                Ext.getApplication().getController('UnidadesController').onAddUnidade();
            }
        }, {
            text: 'Editar',
            iconCls: 'x-fa fa-edit',
            action: 'edit',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid');
                Ext.getApplication().getController('UnidadesController').onEditUnidade(grid);
            },
            bind: {
                disabled: '{!unidadesGrid.selection}'
            }
        }, {
            text: 'Remover',
            iconCls: 'x-fa fa-trash',
            action: 'remove',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid');
                Ext.getApplication().getController('UnidadesController').onRemoveUnidade(grid);
            },
            bind: {
                disabled: '{!unidadesGrid.selection}'
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
        store: 'Unidades',
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
            Ext.getApplication().getController('UnidadesController').onEditUnidade(grid);
        }
    }
});