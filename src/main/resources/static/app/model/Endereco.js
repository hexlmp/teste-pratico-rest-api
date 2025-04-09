Ext.define('App.model.Endereco', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'tipoLogradouro', type: 'string' },
        { name: 'logradouro', type: 'string' },
        { name: 'numero', type: 'string' },
        { name: 'bairro', type: 'string' },
        { name: 'cidade', type: 'auto' } // Objeto cidade completo
        //{ name: 'cidade', reference: 'App.model.Cidade' } // Referência para associação
    ],

    proxy: {
        type: 'rest',
        url: '/protected/api/enderecos',
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
        logradouro: { type: 'presence', message: 'Logradouro é obrigatório' },
        bairro: { type: 'presence', message: 'Bairro é obrigatório' },
        cidadeId: { type: 'presence', message: 'Cidade é obrigatória' }
    }
});