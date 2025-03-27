package fr.miage.bricomerlin.common.exception;

import java.io.Serializable;

/**
 * Exception levée lorsqu'un article n'existe pas dans le catalogue
 */
public class ArticleInexistantException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reference;

    public ArticleInexistantException(String reference) {
        super("L'article avec la référence '" + reference + "' n'existe pas dans le catalogue");
        this.reference = reference;
    }

    public String getReference() {
        return reference;
    }
}