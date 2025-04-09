Ext.define('App.view.main.MainModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.main',

    stores: {
        pessoas: {
            type: 'pessoas',
            autoLoad: true
        },
        cidades: {
            type: 'cidades',
            autoLoad: true
        },
        enderecos: {
            type: 'enderecos',
            autoLoad: true
        }
    }
});