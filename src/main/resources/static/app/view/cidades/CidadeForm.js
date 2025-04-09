Ext.define('App.view.cidades.CidadeForm', {
    extend: 'Ext.form.Panel',
    xtype: 'cidade-form',

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
        xtype: 'textfield',
        name: 'nome',
        fieldLabel: 'Nome',
        allowBlank: false,
        maxLength: 100,
        enforceMaxLength: true
    }, {
        xtype: 'combobox',
        name: 'uf',
        fieldLabel: 'UF',
        allowBlank: false,
        forceSelection: true, // Impede que o usuário digite valores não listados
        queryMode: 'local',
        displayField: 'nome',
        valueField: 'sigla',
        store: {
            fields: ['sigla', 'nome'],
            data: [
                { sigla: 'AC', nome: 'Acre' },
                { sigla: 'AL', nome: 'Alagoas' },
                { sigla: 'AP', nome: 'Amapá' },
                { sigla: 'AM', nome: 'Amazonas' },
                { sigla: 'BA', nome: 'Bahia' },
                { sigla: 'CE', nome: 'Ceará' },
                { sigla: 'DF', nome: 'Distrito Federal' },
                { sigla: 'ES', nome: 'Espírito Santo' },
                { sigla: 'GO', nome: 'Goiás' },
                { sigla: 'MA', nome: 'Maranhão' },
                { sigla: 'MT', nome: 'Mato Grosso' },
                { sigla: 'MS', nome: 'Mato Grosso do Sul' },
                { sigla: 'MG', nome: 'Minas Gerais' },
                { sigla: 'PA', nome: 'Pará' },
                { sigla: 'PB', nome: 'Paraíba' },
                { sigla: 'PR', nome: 'Paraná' },
                { sigla: 'PE', nome: 'Pernambuco' },
                { sigla: 'PI', nome: 'Piauí' },
                { sigla: 'RJ', nome: 'Rio de Janeiro' },
                { sigla: 'RN', nome: 'Rio Grande do Norte' },
                { sigla: 'RS', nome: 'Rio Grande do Sul' },
                { sigla: 'RO', nome: 'Rondônia' },
                { sigla: 'RR', nome: 'Roraima' },
                { sigla: 'SC', nome: 'Santa Catarina' },
                { sigla: 'SP', nome: 'São Paulo' },
                { sigla: 'SE', nome: 'Sergipe' },
                { sigla: 'TO', nome: 'Tocantins' }
            ]
        },
        // Mostra a sigla e o nome no dropdown
        tpl: Ext.create('Ext.XTemplate',
            '<tpl for=".">',
            '<div class="x-boundlist-item">{sigla} - {nome}</div>',
            '</tpl>'
        ),
        // Mostra apenas a sigla no campo selecionado
        displayTpl: Ext.create('Ext.XTemplate',
            '<tpl for=".">',
            '{sigla}',
            '</tpl>'
        ),
        // Validação para garantir que apenas UFs válidas sejam selecionadas
        validator: function(displayValue) {
            //console.log(value);
            //console.log(this.value);
            //console.log(this.getSelection());

            if (!this.value) {
                return 'UF é obrigatória';
            }
            if (this.value.length !== 2) {
                return 'UF deve ter exatamente 2 caracteres';
            }
            return true;
        }
    }],

    buttons: [{
        text: 'Salvar',
        formBind: true,
        iconCls: 'x-fa fa-save',
        handler: function(btn) {
            Ext.getApplication().getController('CidadesController').onSaveCidade(btn);
        }
    }, {
        text: 'Cancelar',
        iconCls: 'x-fa fa-times',
        handler: function(btn) {
            Ext.getApplication().getController('CidadesController').onCancelCidade(btn);
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