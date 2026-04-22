package com.Alacan.demo.Pedido.domain.model;

public enum EstadoPedido {
    

   NUEVO {
        public boolean permiteAgregarItem() { return true; }
        public boolean permiteEliminarItem() { return true; }
        public boolean permiteModificarCantidad() { return true; }
    },

    CONFIRMADO {
        public boolean permiteAgregarItem() { return true; }
        public boolean permiteEliminarItem() { return true; }
        public boolean permiteModificarCantidad() { return true; }
    },

    EN_PREPARACION {
        public boolean permiteAgregarItem() { return false; }
        public boolean permiteEliminarItem() { return false; }
        public boolean permiteModificarCantidad() { return true; } // decisión de negocio
    },

    LISTO {
        public boolean permiteAgregarItem() { return false; }
        public boolean permiteEliminarItem() { return false; }
        public boolean permiteModificarCantidad() { return false; }
    },

    ENVIADO {
        public boolean permiteAgregarItem() { return false; }
        public boolean permiteEliminarItem() { return false; }
        public boolean permiteModificarCantidad() { return false; }
    },

    ENTREGADO {
        public boolean permiteAgregarItem() { return false; }
        public boolean permiteEliminarItem() { return false; }
        public boolean permiteModificarCantidad() { return false; }
    },
    
    CANCELADO {
        public boolean permiteAgregarItem() { return false; }
        public boolean permiteEliminarItem() { return false; }
        public boolean permiteModificarCantidad() { return false; }
    };

    public abstract boolean permiteAgregarItem();
    public abstract boolean permiteEliminarItem();
    public abstract boolean permiteModificarCantidad();
}


/* 
public boolean esFinal() {
    return this == ENTREGADO || this == CANCELADO;
}*/