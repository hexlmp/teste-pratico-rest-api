Ext.define('App.model.FotoPessoa', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int'},
        {name: 'data', type: 'date', dateFormat: 'Y-m-d'},
        {name: 'bucket', type: 'string'},
        {name: 'hash', type: 'string'},
        {name: 'pessoa', reference: 'App.model.Pessoa'}
    ],
    proxy: {
        type: 'rest',
        url: '/protected/api/fotos-pessoa',
        reader: {
            type: 'json',
            rootProperty: 'data'
        },
        writer: {
            type: 'json',
            writeAllFields: true
        }
    }
});