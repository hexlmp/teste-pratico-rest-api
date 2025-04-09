Ext.define('App.view.main.MainController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main',

    requires: [
        'App.view.pessoas.PessoasGrid'
        //'App.view.servidores.ServidoresEfetivosGrid'
    ],

    showPessoasGrid: function() {
        // Recarrega store
        var store = Ext.getStore('Pessoas');
        store.clearFilter();
        store.load();
        var panel = this.lookupReference('mainPanel');
        panel.removeAll();
        panel.add({
            xtype: 'pessoas-grid'
        });
    },

    showServidoresGrid: function() {
        // Recarrega store
        var store = Ext.getStore('ServidoresEfetivos');
        store.clearFilter();
        store.load();
        var panel = this.lookupReference('mainPanel');
        panel.removeAll();
        panel.add({
            xtype: 'servidores-efetivos-grid'
        });
    },

    showServidoresTemporariosGrid: function() {
        // Recarrega store
        var store = Ext.getStore('ServidoresTemporarios');
        store.clearFilter();
        store.load();
        var panel = this.lookupReference('mainPanel');
        panel.removeAll();
        panel.add({
            xtype: 'servidores-temporarios-grid'
        });
    },

    showCidadesGrid: function() {
        var store = Ext.getStore('Cidades');
        store.clearFilter();
        store.load();
        var panel = this.lookupReference('mainPanel');
        panel.removeAll();
        panel.add({
            xtype: 'cidades-grid'
        });
    },

    showEnderecosGrid: function() {
        var store = Ext.getStore('Enderecos');
        store.clearFilter();
        store.load();
        var panel = this.lookupReference('mainPanel');
        panel.removeAll();
        panel.add({
            xtype: 'enderecos-grid'
        });
    },

    showUnidadesGrid:  function() {
        var store = Ext.getStore('Unidades');
        store.clearFilter();
        store.load();
        var panel = this.lookupReference('mainPanel');
        panel.removeAll();
        panel.add({
          xtype: 'unidades-grid'
        });
    },

    showLotacoesGrid: function() {
        var store = Ext.getStore('Lotacoes');
        store.clearFilter();
        store.load();
        var panel = this.lookupReference('mainPanel');
        panel.removeAll();
        panel.add({
            xtype: 'lotacoes-grid'
        });
    },

    // Sesão
    sessionTimer: null, // Referência para o timer
    sessionInterval: null, // Referência para o intervalo

    startSessionTimer: function() {
        var view = this.getView();

        // Para qualquer intervalo existente
        if (this.sessionInterval) {
            clearInterval(this.sessionInterval);
        }

        // Atualiza imediatamente e depois a cada 2 segundos
        this.updateSessionTime();
        this.sessionInterval = setInterval(this.updateSessionTime.bind(this), 2000);
    },

    updateSessionTime: function() {
        var timerComponent = this.getView().down('#sessionTimer');
        var ctrl = this;

        Ext.Ajax.request({
            url: '/public/api/session/remaining-time',
            method: 'GET',
            success: function(response) {
                var result = Ext.JSON.decode(response.responseText);
                if (result && result.remainingTime !== undefined) {
                    var time = result.remainingTime;
                    var text = result.principal + ' - Tempo de sessão restante: ';

                    if (time <= 3) {
                        text += '<span style="color:red;font-weight:bold;">SESSÃO EXPIRADA</span>';
                        // Redirecionar para logout ou mostrar mensagem
                        ctrl.onSessionExpired();
                    } else if (time <= 30) {
                        text += '<span style="color:red;font-weight:bold;">' + time + '</span> segundos ';
                        text += '<a href="#" class="renew-session" style="color:white;background:green;padding:2px 5px;border-radius:3px;text-decoration:none;">Renovar Sessão</a>';
                    } else if (time <= 60) {
                        text += '<span style="color:orange;font-weight:bold;">' + time + '</span> segundos';
                    } else {
                        text += '<span style="font-weight:bold;">' + time + '</span> segundos';
                    }

                    timerComponent.update(text);

                    // Adiciona handler para o link de renovação
                    if (time <= 30) {
                        timerComponent.getEl().down('.renew-session').on('click', function(e) {
                            e.preventDefault();
                            ctrl.renewSession();
                        });
                    }
                }
            },
            failure: function() {
                timerComponent.update('Tempo restante: <span style="color:gray;">--</span>');
            }
        });
    },

    renewSession: function() {
        var timerComponent = this.getView().down('#sessionTimer');

        // Mostrar loading
        timerComponent.update('Renovando sessão...');

        Ext.Ajax.request({
            url: '/protected/api/session/refresh',
            method: 'GET',
            success: function(response) {
                var result = Ext.JSON.decode(response.responseText);
                if (result && result.success) {
                    Ext.toast({
                        html: 'Sessão renovada com sucesso!',
                        timeout: 3000
                    });
                    // Força atualização imediata do timer
                    this.updateSessionTime();
                } else {
                    timerComponent.update('Erro ao renovar sessão. <a href="#" class="renew-session">Tentar novamente</a>');
                    timerComponent.getEl().down('.renew-session').on('click', function(e) {
                        e.preventDefault();
                        this.renewSession();
                    }, this);
                }
            },
            failure: function() {
                timerComponent.update('Falha na conexão. <a href="#" class="renew-session">Tentar novamente</a>');
                timerComponent.getEl().down('.renew-session').on('click', function(e) {
                    e.preventDefault();
                    this.renewSession();
                }, this);
            },
            scope: this
        });
    },
    onLogoutClick: function() {
        Ext.Msg.confirm('Logout', 'Deseja realmente sair do sistema?', function(btn) {
            if (btn === 'yes') {
                // Primeiro faz logout local
                Ext.Ajax.request({
                    url: '/protected/api/session/logout',
                    method: 'GET',
                    success: function(response) {
                        var result = Ext.JSON.decode(response.responseText);

                        // Cria um form invisível para fazer o logout do Keycloak
                        var form = document.createElement('form');
                        form.method = 'GET';
                        form.action = result.logoutUrl;
                        form.style.display = 'none';

                        // Adiciona ao body e submete
                        document.body.appendChild(form);
                        form.submit();
                    },
                    failure: function() {
                        // Em caso de falha, redireciona de qualquer forma
                        window.location.href = '/index.html';
                    }
                });
            }
        });
    },
    onSessionExpired: function() {
        // Mostra mensagem modal informando sobre a sessão expirada
        Ext.Msg.show({
            title: 'Sessão Expirada',
            message: 'Sua sessão expirou. Você será desconectado.',
            buttons: Ext.Msg.OK,
            icon: Ext.Msg.WARNING,
            fn: function() {
                // Executa o logout
                //this.onLogoutClick(true); // Passa true para indicar logout automático
                console.log("logout");
                window.location.href = '/index.html?logout';
            },
            scope: this
        });
    }
});