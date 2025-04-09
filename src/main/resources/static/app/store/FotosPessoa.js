Ext.define('App.store.FotosPessoa', {
    extend: 'Ext.data.Store',
    model: 'App.model.FotoPessoa',
    autoLoad: true,
    pageSize: 20
});