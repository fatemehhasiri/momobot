package net.mauhiz.irc.bot.triggers.cwwb;
/**
 * @author abby
 */
public enum ServerStatus {
    OFF(2), ON(1), UNKOWN(0);
    
    /** L'attribut qui contient la valeur associ� � l'enum */
    private final int code;
    
    /**
     * Le constructeur qui associe une valeur � l'enum
     * 
     * @param code
     */
    private ServerStatus(final int code) {
        this.code = code;
    }
    
    /** @return la valeur de l'enum */
    public int getCode() {
        return code;
    }
    
}
