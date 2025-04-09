Ext.define('App.view.fotos.FotosPessoaGrid', {
    extend: 'Ext.grid.Panel',
    xtype: 'fotos-pessoa-grid',

    requires: [
        'App.store.FotosPessoa'
    ],

    title: 'Fotos de Pessoas',
    store: {
        type: 'fotospessoa'
    },

    columns: [
        {text: 'ID', dataIndex: 'id', width: 50},
        {text: 'Data', dataIndex: 'data', flex: 1, xtype: 'datecolumn', format: 'd/m/Y'},
        {text: 'Bucket', dataIndex: 'bucket', flex: 1},
        {text: 'Hash', dataIndex: 'hash', flex: 1},
        {text: 'Pessoa', dataIndex: 'pessoa.nome', flex: 2}
    ],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            text: 'Adicionar',
            iconCls: 'x-fa fa-plus',
            handler: 'onAddFotoPessoa'
        }, {
            text: 'Editar',
            iconCls: 'x-fa fa-edit',
            handler: 'onEditFotoPessoa',
            bind: {
                disabled: '{!fotosPessoaGrid.selection}'
            }
        }, {
            text: 'Remover',
            iconCls: 'x-fa fa-trash',
            handler: 'onRemoveFotoPessoa',
            bind: {
                disabled: '{!fotosPessoaGrid.selection}'
            }
        }]
    }]
});