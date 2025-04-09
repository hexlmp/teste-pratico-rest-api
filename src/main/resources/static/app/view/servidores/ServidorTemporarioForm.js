Ext.define('App.view.servidores.ServidorTemporarioForm', {
    extend: 'Ext.form.Panel',
    xtype: 'servidor-temporario-form',

    bodyPadding: 15,
    defaults: {
        anchor: '100%',
        labelWidth: 120,
        msgTarget: 'side'
    },

    items: [{
        xtype: 'hiddenfield',
        name: 'pessoaAntiga'
    }, {
        //xtype: 'hiddenfield',
        xtype: 'datefield',
        name: 'dataAdmissaoAntiga',
        hidden: true,
        format: 'd/m/Y',
        submitFormat: 'Y-m-d'
    }, {
        xtype: 'combobox',
        name: 'pessoa',
        itemId: 'pessoaCombo',
        fieldLabel: 'Pessoa',
        displayField: 'nome',
        valueField: 'id',
        //queryMode: 'local',
        triggerCls: 'x-form-search-trigger', // Ícone de lupa nativo do ExtJS
        emptyText: 'Buscar por nome...',
        queryMode: 'remote',  // Busca dinâmica no servidor (ou 'local' se os dados já estiverem carregados)
        typeAhead: true,      // Ativa sugestões conforme digita
        minChars: 2,          // Número mínimo de caracteres para acionar a busca
        queryParam: 'search', // Parâmetro enviado ao servidor (opcional, depende da API)
        pageSize:10,
        forceSelection: true,
        allowBlank: false,
        store: 'PessoasCombo',
        listConfig: {
            getInnerTpl: function() {
                return '{nome} (ID: {id})';
            }
        }
    }, {
        xtype: 'datefield',
        name: 'dataAdmissao',
        fieldLabel: 'Data Admissão',
        allowBlank: false,
        format: 'd/m/Y',
        submitFormat: 'Y-m-d'
    }, {
        xtype: 'datefield',
        name: 'dataDemissao',
        fieldLabel: 'Data Demissão',
        format: 'd/m/Y',
        submitFormat: 'Y-m-d'
    }],

    buttons: [{
        text: 'Salvar',
        formBind: true,
        handler: function(btn) {Ext.getApplication().getController('ServidoresController').onSaveServidorTemporario(btn);}
    }, {
        text: 'Cancelar',
        handler: function(btn) {Ext.getApplication().getController('ServidoresController').onCancelServidorTemporario(btn);}
    }],

    loadRecord: function(record) {
        this.callParent(arguments);

        // Garante que o combobox será configurado após o binding padrão
        Ext.defer(function() {
            var form = this;
            var pessoaCombo = form.down('#pessoaCombo');
            var pessoaData = record.get('pessoa');

            this.down('[name=dataAdmissaoAntiga]').setValue(record.get('dataAdmissao'));

            if (pessoaData && pessoaData.id) {
                // Se a store já estiver carregada
                if (pessoaCombo.getStore().isLoaded()) {
                    pessoaCombo.setValue(pessoaData.id);
                    this.down('[name=pessoaAntiga]').setValue(pessoaData.id);
                }
                // Se a store ainda não estiver carregada
                else {
                    pessoaCombo.getStore().load({
                        callback: function() {
                            pessoaCombo.setValue(pessoaData.id);
                            this.down('[name=pessoaAntiga]').setValue(pessoaData.id);
                        }
                    });
                }
            }
        }, 100, this); // Pequeno delay para garantir que o binding padrão ocorreu
        /*if (record) {
            this.down('[name=pessoaAntiga]').setValue(record.get('pessoa').id);
            this.down('[name=dataAdmissaoAntiga]').setValue(record.get('dataAdmissao'));

            var pessoaCombo = this.down('#pessoaCombo');
            var pessoaId = record.get('pessoa').id;

            if (pessoaCombo.getStore().isLoaded()) {
                pessoaCombo.setValue(pessoaId);
            } else {
                pessoaCombo.getStore().load({
                    callback: function() {
                        pessoaCombo.setValue(pessoaId);
                    }
                });
            }
        }*/
    }
});