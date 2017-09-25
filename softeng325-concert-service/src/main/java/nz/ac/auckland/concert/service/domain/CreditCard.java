package nz.ac.auckland.concert.service.domain;

import nz.ac.auckland.concert.common.dto.CreditCardDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDate;

@Embeddable
@Table(name="CREDIT_CARDS")
public class CreditCard {
    public enum Type {Visa, Master};

    @Column(name="CARD_TYPE")
    @Enumerated(EnumType.STRING)
    private CreditCard.Type _type;

    @Column(name="NAME")
    private String _name;

    @Column(name="NUMBER")
    private String _number;

    @Column(name="EXPIRY_DATE")
    private LocalDate _expiryDate;

    public CreditCard() {}

    public CreditCard(CreditCard.Type type, String name, String number, LocalDate expiryDate) {
        _type = type;
        _name = name;
        _number = number;
        _expiryDate = expiryDate;
    }

    public CreditCard.Type getType() {
        return _type;
    }

    public String getName() {
        return _name;
    }

    public String getNumber() {
        return _number;
    }

    public LocalDate getExpiryDate() {
        return _expiryDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CreditCard))
            return false;
        if (obj == this)
            return true;

        CreditCard rhs = (CreditCard) obj;
        return new EqualsBuilder().
                append(_type, rhs._type).
                append(_name, rhs._name).
                append(_number, rhs._number).
                append(_expiryDate, rhs._expiryDate).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(_type).
                append(_name).
                append(_number).
                append(_expiryDate).
                hashCode();
    }
}
