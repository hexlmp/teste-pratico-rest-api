Ext.define('App.view.servidores.ServidorEfetivoForm', {
    extend: 'Ext.form.Panel',
    xtype: 'servidor-efetivo-form',

    bodyPadding: 15,
    defaults: {
        anchor: '100%',
        labelWidth: 120,
        msgTarget: 'side'
    },

    items: [
    {
        xtype: 'hiddenfield',
        name: 'id'
    },
    {
        xtype: 'hiddenfield',
        name: 'matriculaAntiga'  // Campo para a matrícula original
    },
    {
        xtype: 'hiddenfield',
        name: 'pessoaAntiga'   // Campo para a pessoa original
    },
    {
        xtype: 'textfield',
        name: 'matricula',
        fieldLabel: 'Matrícula',
        allowBlank: false,
        maxLength: 20,
        enforceMaxLength: true
    }, {
        xtype: 'combobox',
        name: 'pessoa',
        itemId: 'pessoaCombo', // Adicione um itemId para facilitar referência
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
        },
        listeners: {
            select: function(combo, record) {
                var form = combo.up('form');
                console.log(record.data);
                form.updatePessoaInfo(record.data);
            }
        }

    },
    {
        xtype: 'displayfield',
        fieldLabel: 'Data Nascimento',
        name: 'pessoaDataNascimento',
        itemId: 'pessoaDataNascimentoField',
        renderer: Ext.util.Format.dateRenderer('d/m/Y'),
        margin: '0 0 2 0'  // Espaçamento vertical mínimo entre campos
    },
    {
        xtype: 'displayfield',
        fieldLabel: 'Sexo',
        name: 'pessoaSexo',
        itemId: 'pessoaSexoField',
        margin: '0 0 2 0'  // Espaçamento vertical mínimo entre campos
    },
    {
        xtype: 'displayfield',
        fieldLabel: 'Nome do Pai',
        name: 'pessoaNomePai',
        itemId: 'pessoaNomePaiField',
        margin: '0 0 2 0'  // Espaçamento vertical mínimo entre campos
    },
    {
        xtype: 'displayfield',
        fieldLabel: 'Nome da Mãe',
        name: 'pessoaNomeMae',
        itemId: 'pessoaNomeMaeField'
    }
    ],

    buttons: [{
        text: 'Salvar',
        formBind: true,
        iconCls: 'x-fa fa-save',
        handler: function(btn) {Ext.getApplication().getController('ServidoresController').onSaveServidorEfetivo(btn);}
    }, {
        text: 'Cancelar',
        iconCls: 'x-fa fa-times',
        handler: function(btn) {Ext.getApplication().getController('ServidoresController').onCancelServidorEfetivo(btn);}
    }],

    beforeSave: function() {
        var form = this;

        if (!form.isValid()) {
            Ext.Msg.alert('Erro', 'Por favor, corrija os campos inválidos.');
            return false;
        }

        return true;

    },
    // Método para atualizar os campos com os dados da pessoa
    updatePessoaInfo: function(pessoaRecord) {
        this.down('#pessoaCombo').setValue(pessoaRecord['id']);
        this.down('#pessoaCombo').setRawValue(pessoaRecord['nome']);
        this.down('#pessoaDataNascimentoField').setValue(pessoaRecord['dataNascimento']);
        this.down('#pessoaSexoField').setValue(pessoaRecord['sexo']);
        this.down('#pessoaNomePaiField').setValue(pessoaRecord['pai']);
        this.down('#pessoaNomeMaeField').setValue(pessoaRecord['mae']);
    },

    loadRecord: function(record) {
        this.callParent(arguments); // Chama o loadRecord padrão


        this.down('[name=matriculaAntiga]').setValue(record.data['matricula']);
        this.down('[name=pessoaAntiga]').setValue(record.data['pessoa']['id']);

        this.updatePessoaInfo(record.data['pessoa']);

        // Garante que o combobox será configurado após o binding padrão
        /*Ext.defer(function() {
            var form = this;
            var pessoaCombo = form.down('#pessoaCombo');
            var pessoaData = record.get('pessoa');

            this.down('[name=matriculaAntiga]').setValue(record.get('matricula'));

            if (pessoaData && pessoaData.id) {
                // Se a store já estiver carregada
                if (pessoaCombo.getStore().isLoaded()) {
                    pessoaCombo.setValue(pessoaData.id);
                    this.down('[name=pessoaAntiga]').setValue(pessoaData.id);

                    // Atualiza os campos de informação da pessoa
                    var pessoaRecord = pessoaCombo.getStore().getById(pessoaData.id);
                    if (pessoaRecord) {
                        this.updatePessoaInfo(pessoaRecord);
                    }

                }
                // Se a store ainda não estiver carregada
                else {
                    pessoaCombo.getStore().load({
                        callback: function() {
                            pessoaCombo.setValue(pessoaData.id);
                            this.down('[name=pessoaAntiga]').setValue(pessoaData.id);

                            // Atualiza os campos de informação da pessoa
                            var pessoaRecord = pessoaCombo.getStore().getById(pessoaData.id);
                            if (pessoaRecord) {
                                this.updatePessoaInfo(pessoaRecord);
                            }
                        }
                    });
                }
            }
        }, 100, this); // Pequeno delay para garantir que o binding padrão ocorreu*/
    }
});