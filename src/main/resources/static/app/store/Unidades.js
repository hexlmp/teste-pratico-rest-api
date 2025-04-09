Ext.define('App.store.Unidades', {
    extend: 'Ext.data.Store',
    model: 'App.model.Unidade',
    autoLoad: true,
    pageSize: 20,
    //remoteFilter: true,

    proxy: {
        type: 'rest',
        url: '/protected/api/unidades',
        reader: {
            type: 'json',
            rootProperty: 'data',
            totalProperty: 'totalCount'
        }
    },
    // Filtro rápido por nome
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