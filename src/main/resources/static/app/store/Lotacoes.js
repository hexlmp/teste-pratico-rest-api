Ext.define('App.store.Lotacoes', {
    extend: 'Ext.data.Store',
    alias: 'store.lotacoes',
    model: 'App.model.Lotacao',

    pageSize: 20,
    remoteSort: true,
    remoteFilter: true,

    sorters: [{
        property: 'dataLotacao',
        direction: 'DESC'
    }],

    proxy: {
        type: 'rest',
        url: '/protected/api/lotacoes',
        reader: {
            type: 'json',
            rootProperty: 'data',
            totalProperty: 'total'
        },
        writer: {
            type: 'json',
            writeAllFields: true
        }
    }
});