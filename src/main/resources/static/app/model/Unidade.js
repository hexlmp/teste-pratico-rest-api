Ext.define('App.model.Unidade', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id', type: 'int'},
        {name: 'nome', type: 'string'},
        {name: 'sigla', type: 'string'},
        { name: 'enderecos', type: 'auto', defaultValue: [] }
    ],
    proxy: {
        type: 'rest',
        url: '/protected/api/unidades',
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
        sigla: { type: 'presence', message: 'Sigla é obrigatória' }
    }
});
