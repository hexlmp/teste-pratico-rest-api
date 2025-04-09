Ext.define('App.store.ServidoresEfetivos', {
    extend: 'Ext.data.Store',
    model: 'App.model.ServidorEfetivo',
    autoLoad: true,  // Para carregar os dados assim que o store for inicializado
    autoSync: true,  // Para sincronizar alterações automaticamente
    pageSize: 20,
    proxy: {
            type: 'rest',
            url: '/protected/api/servidores-efetivos',
            reader: {
                type: 'json',
                rootProperty: 'data',
                totalProperty: 'total'
            }
        }
});