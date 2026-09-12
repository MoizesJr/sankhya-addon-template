package br.com.fabricante.addon.services;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;

import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailVendedorIdempotenciaRepository {

    private static final Logger log = Logger.getLogger(EmailVendedorIdempotenciaRepository.class.getName());

    public boolean jaExisteNaFila(EntityFacade facade, String email, String assunto) throws Exception {
        JdbcWrapper jdbc = null;
        NativeSql sql = null;
        ResultSet rs = null;

        try {
            jdbc = facade.getJdbcWrapper();
            jdbc.openSession();

            sql = new NativeSql(jdbc);
            sql.appendSql("SELECT CODFILA FROM TMDFMG WHERE EMAIL = ? AND CAST(ASSUNTO AS VARCHAR(4000)) = ?");
            sql.addParameter(email);
            sql.addParameter(assunto);
            sql.setMaxRows(1);

            rs = sql.executeQuery();
            return rs.next();
        } catch (Exception e) {
            log.log(Level.WARNING, "Falha ao consultar idempotencia temporaria na TMDFMG.", e);
            throw e;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    log.log(Level.FINE, "Falha ao fechar ResultSet da consulta de idempotencia.", e);
                }
            }
            NativeSql.releaseResources(sql);
            JdbcWrapper.closeSession(jdbc);
        }
    }
}
