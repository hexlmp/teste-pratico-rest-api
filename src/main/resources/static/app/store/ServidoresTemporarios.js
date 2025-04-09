Ext.define('App.store.ServidoresTemporarios', {
    extend: 'Ext.data.Store',
    model: 'App.model.ServidorTemporario',
    autoLoad: true,
    pageSize: 20,
    proxy: {
        type: 'rest',
        url: '/protected/api/servidores-temporarios',
        reader: {
            type: 'json',
            rootProperty: 'data',
            totalProperty: 'total'
        }
    }
});