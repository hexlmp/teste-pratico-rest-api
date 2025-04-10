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
    // Estado da sessão
    sessionData: {
        principal: 'Usuário',
        expireTime: 0,
        lastUpdate: 0
    },

    init: function() {
        // Intercepta todas as respostas AJAX
        Ext.Ajax.on('requestcomplete', this.updateFromHeaders, this);
        Ext.Ajax.on('requestexception', this.updateFromHeaders, this);

        // Inicia o timer de decremento
        this.startSessionTimer();
    },

    updateFromHeaders: function(conn, response) {
        // Atualiza o nome do usuário se estiver no header
        var principal = response.getResponseHeader('X-AccessToken-Principal');
        if (principal) {
            this.sessionData.principal = principal;
        }

        // Atualiza o tempo de expiração se estiver no header
        var expireHeader = response.getResponseHeader('X-AccessToken-Expire');
        if (expireHeader) {
            this.sessionData.expireTime = parseInt(expireHeader);
            this.sessionData.lastUpdate = Math.floor(Date.now() / 1000);
        }

        this.updateTimerDisplay();
    },

    startSessionTimer: function() {
        // Garante que só temos um timer ativo
        if (this.sessionTimer) {
            clearInterval(this.sessionTimer);
        }

        // Atualiza o display a cada segundo
        this.sessionTimer = setInterval(this.updateTimerDisplay.bind(this), 1000);
    },

    updateTimerDisplay: function() {
        // Calcula o tempo restante considerando o tempo decorrido
        var now = Math.floor(Date.now() / 1000);
        var elapsed = now - this.sessionData.lastUpdate;
        var remaining = Math.max(0, this.sessionData.expireTime - elapsed);

        var timerComponent = this.getView().down('#sessionTimer');
        if (!timerComponent) return;

        // Formata a exibição com ícone de usuário
        var userInfo = '<span class="x-fa fa-user" style="margin-right:5px;"></span>' +
                       this.sessionData.principal + ' - ';
        var timeInfo = 'Tempo restante: ';

        if (remaining <= 0) {
            timeInfo = '<span style="color:red;font-weight:bold;">SESSÃO EXPIRADA</span>';
            this.onSessionExpired();
        }
        else if (remaining <= 30) {
            timeInfo += '<span style="color:red;font-weight:bold;">' + remaining + '</span>s ';
            timeInfo += '<a href="#" class="renew-session" style="color:white;background:green;padding:2px 5px;border-radius:3px;text-decoration:none;">Renovar</a>';
        }
        else if (remaining <= 60) {
            timeInfo += '<span style="color:orange;font-weight:bold;">' + remaining + '</span>s';
        }
        else {
            var minutes = Math.floor(remaining / 60);
            var seconds = remaining % 60;
            timeInfo += '<span style="font-weight:bold;">' + minutes + 'm ' + seconds + 's</span>';
        }

        timerComponent.update(userInfo + timeInfo);

        // Configura o handler de renovação se necessário
        if (remaining <= 30) {
            var link = timerComponent.getEl().down('.renew-session');
            if (link) {
                link.on('click', function(e) {
                    e.preventDefault();
                    this.renewSession();
                }, this);
            }
        }
    },

    renewSession: function() {
        var timerComponent = this.getView().down('#sessionTimer');
        if (timerComponent) {
            timerComponent.update('Renovando sessão...');
        }
        Ext.Ajax.request({
            url: '/protected/api/session/refresh',
            method: 'GET',
            success: function(response) {
                this.updateFromHeaders(null, response);

                Ext.Ajax.request({url: '/public/api/session/remaining-time',method: 'GET'});

                Ext.toast({
                    html: 'Sessão renovada com sucesso!',
                    timeout: 3000
                });
            },
            failure: function() {
                Ext.toast({
                    html: 'Erro ao renovar a sessão!',
                    timeout: 3000,
                    cls: 'toast-error'
                });
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