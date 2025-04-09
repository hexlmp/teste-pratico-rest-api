Ext.define('App.store.Cidades', {
    extend: 'Ext.data.Store',
    alias: 'store.cidades',
    model: 'App.model.Cidade',

    autoLoad: true,
    pageSize: 20,

    sorters: [{
        property: 'nome',
        direction: 'ASC'
    }],

    proxy: {
        type: 'rest',
        url: '/protected/api/cidades',
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
                property: 'nome',
                value: text,
                anyMatch: true,
                caseSensitive: false
            }]);
        }
    }
});