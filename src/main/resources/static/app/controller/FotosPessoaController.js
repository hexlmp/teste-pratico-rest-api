Ext.define('App.controller.FotosPessoaController', {
    extend: 'Ext.app.Controller',

    views: [
        'fotos.FotosPessoaGrid',
        'fotos.FotoPessoaForm'
    ],

    stores: [
        'FotosPessoa',
        'Pessoas'
    ],

    init: function() {
        this.control({
            'fotos-pessoa-grid': {
                onAddFotoPessoa: this.onAddFotoPessoa,
                onEditFotoPessoa: this.onEditFotoPessoa,
                onRemoveFotoPessoa: this.onRemoveFotoPessoa
            },
            'foto-pessoa-form': {
                onSaveFotoPessoa: this.onSaveFotoPessoa,
                onCancelFotoPessoa: this.onCancelFotoPessoa
            }
        });
    },

    onAddFotoPessoa: function() {
        var form = Ext.create('App.view.fotos.FotoPessoaForm');
        Ext.create('Ext.window.Window', {
            title: 'Adicionar Foto de Pessoa',
            items: form,
            width: 500,
            modal: true
        }).show();
    },

    onEditFotoPessoa: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];
        var form = Ext.create('App.view.fotos.FotoPessoaForm', {
            record: record
        });
        form.loadRecord(record);
        Ext.create('Ext.window.Window', {
            title: 'Editar Foto de Pessoa',
            items: form,
            width: 500,
            modal: true
        }).show();
    },

    onRemoveFotoPessoa: function(grid) {
        var record = grid.getSelectionModel().getSelection()[0];
        Ext.Msg.confirm('Confirmação', 'Deseja realmente remover esta foto?', function(btn) {
            if (btn === 'yes') {
                grid.getStore().remove(record);
                grid.getStore().sync();
            }
        });
    },

    onSaveFotoPessoa: function(button) {
        var form = button.up('form');
        var record = form.getRecord();
        var values = form.getValues();

        if (!record) {
            record = Ext.create('App.model.FotoPessoa');
        }

        record.set(values);
        form.up('window').close();
        this.getStore('FotosPessoa').add(record);
        this.getStore('FotosPessoa').sync();
    },

    onCancelFotoPessoa: function(button) {
        button.up('window').close();
    }
});