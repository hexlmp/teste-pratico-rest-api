Ext.application({
    name: 'App',
    theme: 'modern',

    extend: 'Ext.app.Application',

    requires: [
            'App.view.main.Viewport',
            'App.view.main.MainController',
            'App.view.main.MainModel'
            //'App.controller.PessoasController',
            //'App.view.pessoas.PessoasGrid',
            //'App.store.Pessoas',
            //'App.controller.ServidoresController',
            //'App.view.servidores.ServidoresEfetivosGrid',
            //'App.view.servidores.ServidorEfetivoForm',
            //'App.view.servidores.ServidoresTemporariosGrid',
            //'App.view.servidores.ServidorTemporarioForm'
    ],

    mainView: 'App.view.main.Viewport',

    controllers: [
        'PessoasController',
        'ServidoresController',
        'FotosPessoaController',
        'UnidadesController',
        'LotacoesController',
        'CidadesController',
        'EnderecosController'
    ],

    stores: [
        'ServidoresEfetivos',
        'ServidoresTemporarios',
        'Unidades',
        'Lotacoes',
        'Pessoas',
        'PessoasCombo',
        'FotosPessoa',
        'Cidades',
        'Enderecos'
    ],

    launch: function() {
        //Ext.create('App.view.Viewport');

        // Carrega os stores necessários
        //this.getStore('Pessoas').load();
        //this.getStore('Unidades').load();
    }
});