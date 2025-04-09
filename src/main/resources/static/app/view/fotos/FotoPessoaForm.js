Ext.define('App.view.fotos.FotoPessoaForm', {
    extend: 'Ext.form.Panel',
    xtype: 'foto-pessoa-form',

    bodyPadding: 10,
    defaults: {
        anchor: '100%',
        labelWidth: 120
    },

    items: [{
        xtype: 'hiddenfield',
        name: 'id'
    }, {
        xtype: 'datefield',
        name: 'data',
        fieldLabel: 'Data',
        format: 'Y-m-d',
        allowBlank: false
    }, {
        xtype: 'textfield',
        name: 'bucket',
        fieldLabel: 'Bucket',
        allowBlank: false
    }, {
        xtype: 'textfield',
        name: 'hash',
        fieldLabel: 'Hash',
        allowBlank: false
    }, {
        xtype: 'combobox',
        name: 'pessoa.id',
        fieldLabel: 'Pessoa',
        displayField: 'nome',
        valueField: 'id',
        store: 'Pessoas',
        queryMode: 'local',
        forceSelection: true,
        allowBlank: false
    }],

    buttons: [{
        text: 'Salvar',
        formBind: true,
        handler: 'onSaveFotoPessoa'
    }, {
        text: 'Cancelar',
        handler: 'onCancelFotoPessoa'
    }]
});