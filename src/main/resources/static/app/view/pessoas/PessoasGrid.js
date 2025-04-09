Ext.define('App.view.pessoas.PessoasGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'pessoas-grid',

    requires: [
        'Ext.grid.column.Column',
        'Ext.toolbar.Paging',
        'App.store.Pessoas',
        //'App.controller.PessoasController'
    ],

    title: 'CRUD - Pessoas',
    store: 'Pessoas',
    // controller: 'pessoas',

    columns: [
        { text: 'ID Pessoa', dataIndex: 'id', width: 100 },
        { text: 'Nome', dataIndex: 'nome', flex: 2 },
        {
            text: 'Nascimento',
            dataIndex: 'dataNascimento',
            xtype: 'datecolumn',
            format: 'd/m/Y',
            width: 120
        },
        { text: 'Sexo', dataIndex: 'sexo', width: 80 },
        { text: 'Mãe', dataIndex: 'mae', flex: 1 },
        { text: 'Pai', dataIndex: 'pai', flex: 1 }
    ],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            text: 'Adicionar',
            iconCls: 'x-fa fa-plus',
            action: 'add',
            handler: function() {Ext.getApplication().getController('PessoasController').onAddPessoa();}
        }, {
            text: 'Editar',
            iconCls: 'x-fa fa-edit',
            action: 'edit',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid'); // Obtém a referência do grid
                Ext.getApplication().getController('PessoasController').onEditPessoa(grid);
            },
            bind: {
                disabled: '{!pessoasGrid.selection}'
            }
        }, {
            text: 'Remover',
            iconCls: 'x-fa fa-trash',
            action: 'remove',
            disabled: true,
            handler: function(btn) {
                var grid = btn.up('grid'); // Obtém a referência do grid
                Ext.getApplication().getController('PessoasController').onRemovePessoa(grid);
            },
            bind: {
                disabled: '{!pessoasGrid.selection}'
            }
        }, '->', {
             xtype: 'textfield',
             emptyText: 'Pesquisar por nome...',
             width: 250,
             listeners: {
                 change: {
                     fn: function(field) {
                         // Correção: Acessar o store corretamente
                         var grid = field.up('grid'); // Navega até o grid
                         var store = grid.getStore(); // Obtém o store do grid

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
                     // Removido o scope: this pois não é mais necessário
                 }
             }
         }
         ]
    }],
    bbar: {
        xtype: 'pagingtoolbar',
        dock: 'bottom',
        store: 'Pessoas',
        displayInfo: true,
        pageSizeSelector: true,  // Habilita o seletor de registros por página
        displayMsg: 'Mostrando {0} - {1} de {2}',
        emptyMsg: 'Nenhum registro encontrado'
    },

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
            Ext.getApplication().getController('PessoasController').onEditPessoa(grid);
        }
    }
});