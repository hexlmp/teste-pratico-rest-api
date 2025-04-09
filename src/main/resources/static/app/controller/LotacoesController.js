Ext.define('App.controller.LotacoesController', {
    extend: 'Ext.app.Controller',

    views: [
        'lotacoes.LotacoesGrid',
        'lotacoes.LotacaoForm'
    ],

    stores: [
        'Lotacoes',
        'Pessoas',
        'Unidades'
    ],

    init: function() {
        this.control({
            'lotacoes-grid': {
                onAddLotacao: this.onAddLotacao,
                onEditLotacao: this.onEditLotacao,
                onRemoveLotacao: this.onRemoveLotacao
            },
            'lotacao-form': {
                onSaveLotacao: this.onSaveLotacao,
                onCancelLotacao: this.onCancelLotacao
            }
        });
    },

    // Métodos para Lotacao
    onAddLotacao: function() {
        var form = Ext.create('App.view.lotacoes.LotacaoForm');
        Ext.create('Ext.window.Window', {
            title: 'Adicionar Lotação',
            items: form,
            width: 500,
            modal: true
        }).show();
    },

    onEditLotacao: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];
        var form = Ext.create('App.view.lotacoes.LotacaoForm', {
            record: record
        });
        form.loadRecord(record);
        Ext.create('Ext.window.Window', {
            title: 'Editar Lotação',
            items: form,
            width: 500,
            modal: true
        }).show();
    },

    onRemoveLotacao: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];
        Ext.Msg.confirm('Confirmação', 'Deseja realmente remover esta lotação?', function(btn) {
            if (btn === 'yes') {
                grid.getStore().remove(record);
                grid.getStore().sync();
            }
        });
    },

    onSaveLotacao: function(button) {
        var form = button.up('form');
        var record = form.getRecord();
        var values = form.getValues();

        if (!record) {
            record = Ext.create('App.model.Lotacao');
        }

        record.set(values);
        form.up('window').close();
        this.getStore('Lotacoes').add(record);
        this.getStore('Lotacoes').sync();
    },

    onCancelLotacao: function(button) {
        button.up('window').close();
    }
});