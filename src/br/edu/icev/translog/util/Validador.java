package br.edu.icev.translog.util;

public class Validador {

    //validando digitos verificadores CPF
    public static boolean isCPF(String cpf) {
        //limpa entrada
        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false; //tamanho errado ou numero repetido
        }

        try {
            int sm, r, num, peso;

            //1 digito verificador
            sm = 0;
            peso = 10;
            for (int i = 0; i < 9; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            char dig10 = (r == 10 || r == 11) ? '0' : (char) (r + 48);

            //2 digito verificador
            sm = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            char dig11 = (r == 10 || r == 11) ? '0' : (char) (r + 48);

            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
            
        } catch (Exception e) {
            return false;
        }
    }

    
    //validando digitos verificadores CNPJ
    public static boolean isCNPJ(String cnpj) {
        cnpj = cnpj.replaceAll("\\D", "");

        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        try {
            int sm, r, num, peso;
            char dig13, dig14;

            // 1º Dígito
            sm = 0;
            peso = 2;
            for (int i = 11; i >= 0; i--) {
                num = (int) (cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10) peso = 2;
            }

            r = sm % 11;
            dig13 = (r == 0 || r == 1) ? '0' : (char) ((11 - r) + 48);

            // 2º Dígito
            sm = 0;
            peso = 2;
            for (int i = 12; i >= 0; i--) {
                num = (int) (cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10) peso = 2;
            }

            r = sm % 11;
            dig14 = (r == 0 || r == 1) ? '0' : (char) ((11 - r) + 48);

            return (dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13));
            
        } catch (Exception e) {
            return false;
        }
    }

    
    //valida telefone brasileiro usando regex
    public static boolean isTelefone(String telefone) {
        // Remove tudo que não é número
        String numeros = telefone.replaceAll("\\D", "");
        
        //verifica se tem 10 (fixo) ou 11 (celular)
        //verifica se o dd é valido , maior que 10 e menor que 99
        return numeros.length() >= 10 && numeros.length() <= 11;
    }
    public static boolean isCNH(String cnh) {
        //limpa entrada
        cnh = cnh.replaceAll("\\D", "");

        //valida se tem 11 digitos
        if (cnh.length() != 11) {
            return false;
        }
        //vlaida se nao sao todos iguais
        if (cnh.matches("(\\d)\\1{10}")) {
            return false;
        }
        //valida se passa nos dois testes
        return true;
    }
}