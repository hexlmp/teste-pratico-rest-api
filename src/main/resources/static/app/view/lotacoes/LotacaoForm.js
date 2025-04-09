Ext.define('App.view.lotacoes.LotacaoForm', {
    extend: 'Ext.form.Panel',
    xtype: 'lotacao-form',

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
            xtype: 'combobox',
            name: 'pessoa',
            fieldLabel: 'Pessoa',
            displayField: 'nome',
            valueField: 'id',
            queryMode: 'remote',
            triggerCls: 'x-form-search-trigger',
            emptyText: 'Buscar por nome...',
            minChars: 2,
            forceSelection: true,
            allowBlank: false,
            store: 'Pessoas',
            listConfig: {
                getInnerTpl: function() {
                    return '{nome} (ID: {id})';
                }
            }
        },
        {
            xtype: 'combobox',
            name: 'unidade',
            fieldLabel: 'Unidade',
            displayField: 'nome',
            valueField: 'id',
            queryMode: 'remote',
            triggerCls: 'x-form-search-trigger',
            emptyText: 'Buscar por nome...',
            minChars: 2,
            forceSelection: true,
            allowBlank: false,
            store: 'Unidades',
            listConfig: {
                getInnerTpl: function() {
                    return '{nome} ({sigla})';
                }
            }
        },
        {
            xtype: 'datefield',
            name: 'dataLotacao',
            fieldLabel: 'Data Lotação',
            format: 'd/m/Y',
            submitFormat: 'Y-m-d',
            allowBlank: false
        },
        {
            xtype: 'datefield',
            name: 'dataRemocao',
            fieldLabel: 'Data Remoção',
            format: 'd/m/Y',
            submitFormat: 'Y-m-d'
        },
        {
            xtype: 'textfield',
            name: 'portaria',
            fieldLabel: 'Portaria',
            allowBlank: false
        }
    ],

    buttons: [{
        text: 'Salvar',
        formBind: true,
        iconCls: 'x-fa fa-save',
        handler: 'onSaveLotacao'
    }, {
        text: 'Cancelar',
        iconCls: 'x-fa fa-times',
        handler: 'onCancelLotacao'
    }]
});