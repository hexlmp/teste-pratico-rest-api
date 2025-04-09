Ext.define('App.model.ServidorEfetivo', {
    extend: 'Ext.data.Model',
    //idProperty: false, // Desativa o ID automático do ExtJS
    identifier: 'sequential',
    fields: [
        {name: 'matricula', type: 'string'}
        // {name: 'pessoa', type: 'auto'}
        // {name: 'pessoa', mapping:'pessoa', type: 'auto'}
    ],
    associations: [{
            type: 'hasOne',
            model: 'Pessoa',
            name: 'pessoa',
            associationKey: 'pessoa'
        }],
    proxy: {
        type: 'rest',
        url: '/protected/api/servidores-efetivos',
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
