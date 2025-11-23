🚚 Sistema Translog - Gestão Logística

Um sistema robusto desenvolvido em Java para gerenciamento de transportes, agendamento de cargas e automação de processos logísticos.

📋 Sobre o Projeto

O Translog foi desenvolvido como trabalho final da disciplina de Programação Orientada a Objetos. O objetivo é resolver problemas reais de uma transportadora, como conflitos de agenda de motoristas, cálculos imprecisos de frete e falta de validação de dados.

O sistema utiliza uma arquitetura MVC (Model-View-Controller) para garantir organização, manutenibilidade e separação de responsabilidades.

✨ Funcionalidades Principais

1. Cadastros Inteligentes

Clientes: Suporte a clientes Empresariais (CNPJ) e Prioritários (CPF), com regras de negócio específicas (Polimorfismo).

Motoristas: Validação de formato da CNH.

Validação Real: Algoritmos matemáticos (Módulo 11) verificam a autenticidade de CPFs e CNPJs em tempo real, impedindo dados falsos.

2. Logística e Cálculo de Frete

Classificação Automática: O sistema define se a carga é Leve, Média ou Pesada baseado apenas no peso digitado.

Cálculo Preciso: Considera distância, fator de peso e adicionais de risco (Carga Frágil/Perigosa +40%).

Agendamento Seguro: Bloqueio dinâmico de agenda. Uma viagem de 300km bloqueia o motorista por 3 horas, impedindo conflitos de horário.

3. Persistência de Dados

Banco em Arquivo: Todos os dados são salvos automaticamente em arquivos CSV (banco_clientes.csv, etc.) ao fechar o programa.

Recuperação: Ao abrir, o sistema carrega o histórico completo, incluindo entregas passadas.

Notas Fiscais: Geração automática de comprovantes em .txt na pasta notas_fiscais.

🛠️ Tecnologias Utilizadas

Linguagem: Java (JDK 8 ou superior)

Interface Gráfica: Swing (Java Foundation Classes)

Persistência: Arquivos de Texto (CSV) com java.io

Data e Hora: API java.time (LocalDateTime)

🚀 Como Executar o Projeto

Pré-requisitos

Ter o Java JDK instalado na máquina.

Passo a Passo

Clone o repositório (ou baixe o ZIP):

git clone [https://github.com/DhianSepedro/sistemaTranslog.git](https://github.com/DhianSepedro/sistemaTranslog.git)


Compile o projeto:
Navegue até a pasta src e execute:

javac br/edu/icev/translog/main/Main.java


Execute a aplicação:

java br.edu.icev.translog.main.Main


📂 Estrutura do Projeto (MVC)

src/br/edu/icev/translog/
├── model/           # Classes de Domínio (Cliente, Entrega, Carga)
├── view/            # Telas (JanelaPrincipal, Paineis de Cadastro)
├── service/         # Regras de Negócio (Cálculo, Validações)
├── repository/      # Leitura e Escrita de Arquivos CSV
├── util/            # Utilitários (Validador de CPF/CNPJ)
└── main/            # Ponto de Entrada (Main.java)


🧠 Destaques de Implementação (POO)

Polimorfismo: O cálculo de desconto é delegado para a classe do Cliente (obterDesconto()), permitindo que o sistema cresça sem complexidade.

Encapsulamento: Atributos protegidos e acesso controlado via métodos.

Tratamento de Erros: Uso extensivo de try-catch para garantir que o sistema não feche inesperadamente ao receber dados inválidos.
