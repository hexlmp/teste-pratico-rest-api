Ext.define('App.model.ServidorTemporario', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'pessoa', type: 'auto'},
        {name: 'dataAdmissao', type: 'date', dateFormat: 'Y-m-d'},
        {name: 'dataDemissao', type: 'date', dateFormat: 'Y-m-d'}
        //{name: 'pessoaAntiga', type: 'auto'},
        //{name: 'dataAdmissaoAntiga', type: 'date', dateFormat: 'Y-m-d'}
    ],
    proxy: {
        type: 'rest',
        url: '/protected/api/servidores-temporarios',
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