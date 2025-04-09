Ext.define('App.store.PessoasCombo', {
    extend: 'Ext.data.Store',
    alias: 'store.pessoasCombo',
    model: 'App.model.Pessoa',

    autoLoad: true,
    pageSize: 20,

    sorters: [{
        property: 'nome',
        direction: 'ASC'
    }],

    proxy: {
        type: 'rest',
        url: '/protected/api/pessoas',
        reader: {
            type: 'json',
            rootProperty: 'data',
            totalProperty: 'total'
        },
        writer: {
            type: 'json',
            writeAllFields: true
        },
        extraParams: {
            search: '' // Parâmetro padrão (será sobrescrito durante a digitação no livesearch)
        }
    },

    // Filtro rápido por nome
    quickFilter: function(text) {
        this.clearFilter();

        if (text) {
            this.filter([{
                property: 'nome',
                value: text,
                anyMatch: true,
                caseSensitive: false
            }]);
        }
    }
});