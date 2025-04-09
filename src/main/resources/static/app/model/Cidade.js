Ext.define('App.model.Cidade', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'nome', type: 'string' },
        { name: 'uf', type: 'string' }
    ],

    proxy: {
        type: 'rest',
        url: '/protected/api/cidades',
        reader: {
            type: 'json',
            rootProperty: 'data'
        },
        writer: {
            type: 'json',
            writeAllFields: true
        }
    },

    validators: {
        nome: { type: 'presence', message: 'Nome é obrigatório' },
        uf: { type: 'presence', message: 'UF é obrigatória' }
    }
});