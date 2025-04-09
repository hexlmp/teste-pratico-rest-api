Ext.define('App.model.Lotacao', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'dataLotacao', type: 'date', dateFormat: 'Y-m-d' },
        { name: 'dataRemocao', type: 'date', dateFormat: 'Y-m-d' },
        { name: 'portaria', type: 'string' },
        { name: 'pessoa', type: 'auto' },  // Associação com Pessoa
        { name: 'unidade', type: 'auto' }  // Associação com Unidade
    ],

    proxy: {
        type: 'rest',
        url: '/protected/api/lotacoes',
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
        pessoa: { type: 'presence', message: 'Pessoa é obrigatória' },
        unidade: { type: 'presence', message: 'Unidade é obrigatória' },
        dataLotacao: { type: 'presence', message: 'Data de lotação é obrigatória' }
    }
});