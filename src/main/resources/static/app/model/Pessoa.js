Ext.define('App.model.Pessoa', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'id', type: 'int' },
        { name: 'nome', type: 'string' },
        { name: 'dataNascimento', type: 'date', dateFormat: 'Y-m-d' },
        { name: 'sexo', type: 'string' },
        { name: 'mae', type: 'string' },
        { name: 'pai', type: 'string' },
        { name: 'fotos', type: 'auto', defaultValue: [] },
        { name: 'tempFotoIds', type: 'auto', defaultValue: [] }
    ],

    proxy: {
        type: 'rest',
        url: '/protected/api/pessoas',
        reader: {
            type: 'json',
            rootProperty: 'data'
        },
        writer: {
            type: 'json',
            writeAllFields: true
        }
    },
    // Filtro remoto automático (acionado pelo combobox)
    remoteFilter: true,

    validators: {
        nome: { type: 'presence', message: 'Nome é obrigatório' },
        dataNascimento: { type: 'presence', message: 'Data de nascimento é obrigatória' }
    }
});