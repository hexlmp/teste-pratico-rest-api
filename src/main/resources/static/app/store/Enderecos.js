Ext.define('App.store.Enderecos', {
    extend: 'Ext.data.Store',
    alias: 'store.enderecos',
    model: 'App.model.Endereco',

    autoLoad: true,
    pageSize: 20,

    sorters: [{
        property: 'logradouro',
        direction: 'ASC'
    }],

    proxy: {
        type: 'rest',
        url: '/protected/api/enderecos',
        reader: {
            type: 'json',
            rootProperty: 'data',
            totalProperty: 'total'
        },
        writer: {
            type: 'json',
            writeAllFields: true
        }
    },

    quickFilter: function(text) {
        this.clearFilter();

        if (text) {
            this.filter([{
                property: 'logradouro',
                value: text,
                anyMatch: true,
                caseSensitive: false
            }]);
        }
    }
});