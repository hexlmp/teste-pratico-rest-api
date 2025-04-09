Ext.define('App.view.enderecos.EnderecoForm', {
    extend: 'Ext.form.Panel',
    xtype: 'endereco-form',

    bodyPadding: 20,
    defaults: {
        anchor: '100%',
        labelWidth: 120,
        msgTarget: 'side'
    },

    items: [{
        xtype: 'hiddenfield',
        name: 'id'
    }, {
        xtype: 'combobox',
        name: 'tipoLogradouro',
        fieldLabel: 'Tipo Logradouro',
        store: ['Rua', 'Avenida', 'Travessa', 'Praça', 'Alameda', 'Rodovia', 'Estrada'],
        editable: false,
        allowBlank: false
    }, {
        xtype: 'textfield',
        name: 'logradouro',
        fieldLabel: 'Logradouro',
        allowBlank: false,
        maxLength: 100,
        enforceMaxLength: true
    }, {
        xtype: 'textfield',
        name: 'numero',
        fieldLabel: 'Número',
        allowBlank: true,
        maxLength: 10,
        enforceMaxLength: true,
        emptyText: 'S/N'
    }, {
        xtype: 'textfield',
        name: 'bairro',
        fieldLabel: 'Bairro',
        allowBlank: false,
        maxLength: 50,
        enforceMaxLength: true
    }, {
        xtype: 'combobox',
        name: 'cidade',
        fieldLabel: 'Cidade',
        allowBlank: false,
        forceSelection: true,
        queryMode: 'remote',
        displayField: 'nome',
        valueField: 'id',
        store: 'Cidades',
        tpl: Ext.create('Ext.XTemplate',
            '<tpl for=".">',
            '<div class="x-boundlist-item">{nome} - {uf}</div>',
            '</tpl>'
        ),
        displayTpl: Ext.create('Ext.XTemplate',
            '<tpl for=".">',
            '{nome} - {uf}',
            '</tpl>'
        ),
        minChars: 1,
        pageSize: true,
        listConfig: {
            loadingText: 'Procurando...',
            emptyText: 'Nenhuma cidade encontrada'
        },
        listeners: {
            beforequery: function(queryPlan) {
                queryPlan.query = queryPlan.query.trim();
            }
        }
    }],

    buttons: [{
        text: 'Salvar',
        formBind: true,
        iconCls: 'x-fa fa-save',
        handler: function(btn) {
            Ext.getApplication().getController('EnderecosController').onSaveEndereco(btn);
        }
    }, {
        text: 'Cancelar',
        iconCls: 'x-fa fa-times',
        handler: function(btn) {
            Ext.getApplication().getController('EnderecosController').onCancelEndereco(btn);
        }
    }],

    beforeSave: function() {
        var form = this;

        if (!form.isValid()) {
            Ext.Msg.alert('Erro', 'Por favor, corrija os campos inválidos.');
            return false;
        }

        return true;
    }
});