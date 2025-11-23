# 🚚 Sistema Translog - Gestão de Transportadora

Sistema completo de gerenciamento logístico para a transportadora Translog, desenvolvido em Java com interface gráfica (Swing) e persistência em CSV.

## 📋 Sobre o Projeto

O Sistema Translog foi desenvolvido como projeto acadêmico da disciplina de Programação Orientada a Objetos (POO), implementando uma solução completa para gerenciamento de entregas, cálculo de fretes e controle de agendamentos.

### 🎯 Problema Resolvido

A transportadora "Translog" atende clientes empresariais para entregas de produtos em diversas cidades. O sistema resolve:
- Alocação de motoristas
- Cálculo de custos de frete por distância, peso e tipo de cliente
- Controle de rotas e agendamentos
- Bloqueio automático de horários para evitar conflitos
- Emissão de notas fiscais

---

## ✨ Funcionalidades Principais

### 1. 👥 Cadastro de Clientes
- **Clientes Empresariais**: 10% de desconto em entregas regulares
- **Clientes Prioritários**: 20% de desconto
- Validação de CPF/CNPJ com dígitos verificadores
- Validação de telefone

### 2. 🚗 Cadastro de Motoristas
- Cadastro completo com CNH
- Validação de CNH (11 dígitos)
- Controle de disponibilidade automático

### 3. 📦 Gestão de Cargas
- **Classificação automática por peso:**
  - Leve (até 10kg): R$ 0,92/km × 1.0
  - Média (10-100kg): R$ 0,92/km × 1.5
  - Pesada (acima de 100kg): R$ 0,92/km × 2.0
- **Cargas especiais** (frágeis/perigosas): adicional de 40%

### 4. 📅 Sistema de Agendamento Inteligente
- Bloqueio automático de horários (1 hora a cada 100km)
- Prevenção de conflitos de rota
- Validação de disponibilidade do motorista
- Máscara de data/hora no formulário

### 5. 💰 Cálculo de Frete
**Fórmula:**
```
Valor Base = Distância × R$ 0,92 × Fator do Tipo
+ Adicional (se especial): Valor Base × 40%
- Desconto do Cliente: (Base + Adicional) × 10% ou 20%
```

### 6. 📄 Emissão de Notas Fiscais
- Geração automática em formato TXT
- Discriminação completa dos serviços
- Salvamento organizado na pasta `notas_fiscais/`

### 7. 💾 Persistência de Dados
- Salvamento automático em CSV ao fechar o sistema
- Carregamento automático ao iniciar
- Relacionamentos mantidos via CPF/CNPJ e CNH

---

## 🏗️ Arquitetura do Sistema

### Estrutura de Pacotes

```
br.edu.icev.translog/
├── model/              # Entidades de domínio
│   ├── Cliente.java (abstrata)
│   ├── ClienteEmpresarial.java
│   ├── ClientePrioritario.java
│   ├── Motorista.java
│   ├── Carga.java
│   ├── TipoCarga.java (enum)
│   └── Entrega.java
│
├── service/            # Lógica de negócio
│   ├── CalculadoraFrete.java
│   ├── Agendamento.java
│   └── NotaFiscal.java
│
├── persistencia/       # Camada de dados (CSV)
│   ├── ClienteRepository.java
│   ├── MotoristaRepository.java
│   └── EntregaRepository.java
│
├── view/               # Interface gráfica (Swing)
│   ├── JanelaPrincipal.java
│   ├── PanelCadastro.java
│   ├── PanelEntrega.java
│   ├── PanelListagem.java
│   └── MenuConsole.java (modo console - legado)
│
├── util/               # Utilitários
│   └── Validador.java
│
└── main/
    └── Main.java       # Ponto de entrada
```

### Princípios POO Aplicados

✅ **Encapsulamento**: Atributos privados com getters/setters  
✅ **Herança**: `Cliente` → `ClienteEmpresarial` / `ClientePrioritario`  
✅ **Polimorfismo**: Método `obterDesconto()` implementado diferente em cada tipo  
✅ **Abstração**: Classes abstratas e separação de responsabilidades  
✅ **Enum**: `TipoCarga` com comportamento associado

---

## 🚀 Como Executar

### Pré-requisitos
- **Java JDK 8** ou superior
- Nenhuma biblioteca externa necessária (usa apenas Java SE + Swing)

### Opção 1: Via IDE (Eclipse, IntelliJ, NetBeans)
1. Clone ou baixe o projeto
2. Importe como projeto Java existente
3. Execute a classe `Main.java`

### Opção 2: Via Linha de Comando

#### Compilar
```bash
# Navegar até a pasta src
cd src

# Compilar todos os arquivos
javac -encoding UTF-8 br/edu/icev/translog/**/*.java

# Ou compilar recursivamente (Windows)
javac -encoding UTF-8 br\edu\icev\translog\**\*.java
```

#### Executar
```bash
# A partir da pasta src
java br.edu.icev.translog.main.Main
```

---

## 📊 Regras de Negócio

### Cálculo de Frete - Exemplo Prático

**Cenário:**
- Cliente: Prioritário (20% desconto)
- Carga: Pesada (100kg) + Perigosa
- Distância: 150 km

**Cálculo:**
```
1. Valor Base:
   150km × R$ 0,92 × 2.0 (fator pesada) = R$ 276,00

2. Adicional Carga Especial (40%):
   R$ 276,00 × 0,40 = R$ 110,40
   Subtotal: R$ 386,40

3. Desconto Cliente Prioritário (20%):
   R$ 386,40 × 0,20 = R$ 77,28

4. Valor Final:
   R$ 386,40 - R$ 77,28 = R$ 309,12
```

### Sistema de Bloqueio de Horários

O sistema calcula automaticamente a duração estimada da entrega:
- **1 hora de bloqueio a cada 100km** (arredonda para cima)
- Exemplos:
  - 50km → 1 hora bloqueada
  - 150km → 2 horas bloqueadas
  - 250km → 3 horas bloqueadas

Se um motorista já tem entrega agendada que conflita com o novo horário, o sistema **rejeita o agendamento** e exibe mensagem de erro.

---

## 📁 Estrutura de Arquivos Gerados

```
projeto/
├── banco_clientes.csv          # Clientes cadastrados
├── banco_motoristas.csv        # Motoristas cadastrados
├── relatorio_entregas.csv      # Histórico de entregas
└── notas_fiscais/              # Notas fiscais individuais
    ├── NF_ClienteA_1234567890.txt
    └── NF_ClienteB_0987654321.txt
```

### Formato dos CSVs

**banco_clientes.csv:**
```csv
TIPO;NOME;DOC;TELEFONE
EMPRESARIAL;Empresa ABC;12345678000199;1199998888
PRIORITARIO;João Silva;12345678900;1188887777
```

**banco_motoristas.csv:**
```csv
NOME;CNH
José Santos;12345678901
Maria Oliveira;98765432109
```

**relatorio_entregas.csv:**
```csv
DOC_CLIENTE;CNH_MOTORISTA;DATA;PESO;TIPO;ESPECIAL;VALOR;DISTANCIA
12345678000199;12345678901;2024-11-23T14:30;50.5;PESADA;true;309.12;150.0
```

---

## 🎨 Interface do Sistema

### Tela 1: Cadastros
- Formulário duplo (Cliente e Motorista lado a lado)
- Seleção de tipo (Empresarial/Prioritário) com radio buttons
- Validação em tempo real de CPF/CNPJ

### Tela 2: Nova Entrega
- Seleção de cliente e motorista via combobox
- **Classificação automática** da carga ao digitar o peso
- Checkbox para carga especial
- Campo de data/hora com máscara (dd/MM/yyyy HH:mm)
- Cálculo em tempo real do valor
- Botão de confirmação com validação de conflitos

### Tela 3: Relatório de Entregas
- Tabela com todas as entregas agendadas
- Botão para atualizar visualização
- Botão para salvar tudo em CSV

---

## 🧪 Validações Implementadas

### CPF (Dígitos Verificadores)
```java
// Algoritmo completo de validação
// Verifica os dois dígitos verificadores
```

### CNPJ (Dígitos Verificadores)
```java
// Algoritmo completo de validação
// Verifica os dois dígitos verificadores
```

### CNH
- Formato: 11 dígitos numéricos
- Não pode ter todos os dígitos iguais

### Telefone
- Aceita 10 dígitos (fixo) ou 11 dígitos (celular)
- Formato: (XX) XXXXX-XXXX ou (XX) XXXX-XXXX

---

## 🛠️ Tecnologias Utilizadas

- **Java SE 8+**: Linguagem principal
- **Swing**: Interface gráfica
- **BufferedReader/BufferedWriter**: Manipulação de arquivos CSV
- **LocalDateTime**: Gestão de datas e horários
- **Regex**: Validações de documentos

---

## 📝 Modo Console (Legado)

O sistema também possui uma versão console, mantida comentada no `Main.java` para fins acadêmicos. Para usar:

1. Comente o bloco da interface gráfica
2. Descomente o bloco do `MenuConsole`
3. Execute novamente

---

## 👥 Equipe

**Desenvolvido por:**
- [Dhian Pablo Dias Sepedro]
- [Gabriel Leal Batista]
- [Arthur Hagel Porfirio Mendes]

**Disciplina:** Programação Orientada a Objetos  
**Instituição:** ICEV  
**Período:** 2024.2  
**Professor:** [Samuel Vinicius Pereira de Oliveira]

---

## 📄 Licença

Projeto acadêmico - todos os direitos reservados aos autores.

---

## 🎯 Diferenciais do Projeto

✨ **Interface Gráfica Completa** - Poucos grupos implementaram GUI  
✨ **Validações Robustas** - CPF/CNPJ com algoritmos corretos  
✨ **Sistema de Bloqueio Inteligente** - Previne conflitos reais  
✨ **Classificação Automática** - Facilita uso do sistema  
✨ **Persistência Funcional** - Dados mantidos entre sessões  

---
