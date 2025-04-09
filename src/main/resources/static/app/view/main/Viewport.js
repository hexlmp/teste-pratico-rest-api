Ext.define('App.view.main.Viewport', {
    extend: 'Ext.container.Viewport',
    xtype: 'app-mainviewport',

    requires: [
        'App.view.pessoas.PessoasGrid',
        'App.view.servidores.ServidoresEfetivosGrid',
        'App.view.servidores.ServidoresTemporariosGrid',
        'App.view.cidades.CidadesGrid',
        'App.view.enderecos.EnderecosGrid'
    ],

    controller: 'main',
    viewModel: 'main',

    layout: 'border',

    items: [
        {
            region: 'north',
            xtype: 'toolbar',
            height: 60,
            items: [
                {
                    xtype: 'button',
                    text: 'CRUD - Pessoas',
                    iconCls: 'x-fa fa-users',
                    enableToggle: true,
                    toggleGroup: 'toolbarCRUD',
                    pressed: true, // Faz o botão começar selecionado
                    handler: 'showPessoasGrid'
                }, {
                    xtype: 'button',
                    text: 'CRUD - Servidores Efetivos',
                    iconCls: 'x-fa fa-id-card',
                    enableToggle: true,
                    toggleGroup: 'toolbarCRUD',
                    handler: 'showServidoresGrid'
                },
                {
                    xtype: 'button',
                    text: 'CRUD - Servidores Temporários',
                    iconCls: 'x-fa fa-clock',
                    enableToggle: true,
                    toggleGroup: 'toolbarCRUD',
                    handler: 'showServidoresTemporariosGrid'
                },
                {
                    xtype: 'button',
                    text: 'CRUD - Cidades',
                    iconCls: 'x-fa fa-city',
                    enableToggle: true,
                    toggleGroup: 'toolbarCRUD',
                    handler: 'showCidadesGrid'
                },
                {
                    xtype: 'button',
                    text: 'CRUD - Endereços',
                    iconCls: 'x-fa fa-map-marker-alt',
                    enableToggle: true,
                    toggleGroup: 'toolbarCRUD',
                    handler: 'showEnderecosGrid'
                },
                {
                    xtype: 'button',
                    text: 'CRUD - Unidades',
                    iconCls: 'x-fa fa-building',
                    enableToggle: true,
                    toggleGroup: 'toolbarCRUD',
                    handler: 'showUnidadesGrid'
                },
                {
                    xtype: 'button',
                    text: 'CRUD - Lotações',
                    iconCls: 'x-fa fa-exchange-alt',
                    enableToggle: true,
                    toggleGroup: 'toolbarCRUD',
                    handler: 'showLotacoesGrid'
                }
                ,
                '->',
                { // Alinha à direita
                    xtype: 'component',
                    itemId: 'sessionTimer',
                    html: 'Tempo restante da sessão: <span style="font-weight:bold;">--</span> segundos',
                    margin: '0 10 0 0'
                },
                { // Botão de logout
                    xtype: 'button',
                    text: 'Logout',
                    iconCls: 'x-fa fa-sign-out-alt',
                    //margin: '0 10 0 0',
                    handler: 'onLogoutClick',
                    /*style: {
                        backgroundColor: '#f44336',
                        color: 'white',
                        borderColor: '#d32f2f'
                    },
                    listeners: {
                        mouseover: function(btn) {
                            btn.setStyle({
                                backgroundColor: '#d32f2f',
                                color: 'white'
                            });
                        },
                        mouseout: function(btn) {
                            btn.setStyle({
                                backgroundColor: '#f44336',
                                color: 'white'
                            });
                        }
                    }*/
                }
            ]
        },
        {
            region: 'center',
            xtype: 'panel',
            reference: 'mainPanel',
            layout: 'fit',
            items: [{
                xtype: 'pessoas-grid'
            }]
        }
    ],
    listeners: {
        afterrender: 'startSessionTimer'
    }
});