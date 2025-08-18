# 📦 Estoque Fácil

Sistema de Controle de Estoque desenvolvido em Java EE com interface moderna e funcionalidades avançadas de gestão de produtos, usuários e pagamentos.

## 🚀 Visão Geral

O **Estoque Fácil** é uma aplicação web completa para controle de estoque que permite gerenciar produtos, usuários e pagamentos de forma eficiente e intuitiva. O sistema oferece controle de validade de produtos, gestão de usuários e registro de transações com interface moderna e responsiva.

## 🏗️ Arquitetura

### Stack Tecnológica
- **Backend**: Java EE 8
- **Frontend**: JSF (JavaServer Faces) 2.2.20 + PrimeFaces 10.0.0
- **Persistência**: Hibernate 5.6.5 + PostgreSQL 13
- **Build**: Maven 3.8
- **Containerização**: Docker Compose
- **Pool de Conexões**: C3P0

### Padrões Arquiteturais
- **MVC**: Model-View-Controller com JSF
- **DAO**: Data Access Object para abstração da camada de dados
- **Managed Beans**: Para controle de estado das páginas
- **JPA/Hibernate**: Para mapeamento objeto-relacional

## 📁 Estrutura do Projeto

```
estoque-facil/
├── src/main/java/org/devilish/
│   ├── bean/          # Managed Beans JSF
│   │   ├── DashboardBean.java
│   │   ├── PaymentBean.java
│   │   ├── ProductBean.java
│   │   └── UserBean.java
│   ├── dao/           # Interfaces DAO
│   │   ├── PaymentDAO.java
│   │   ├── ProductDAO.java
│   │   └── UserDAO.java
│   ├── entity/        # Entidades JPA
│   │   ├── Payment.java
│   │   ├── Product.java
│   │   └── User.java
│   ├── exceptions/    # Exceções customizadas
│   │   ├── BusinessException.java
│   │   └── DAOException.java
│   ├── impl/          # Implementações dos DAOs
│   │   ├── PaymentDAOImpl.java
│   │   ├── ProductDAOImpl.java
│   │   └── UserDAOImpl.java
│   ├── util/          # Utilitários
│   │   └── HibernateUtil.java
│   └── Main.java
├── src/main/webapp/
│   ├── resources/css/ # Estilos CSS
│   │   ├── dashboard.css
│   │   ├── layout.css
│   │   ├── payments.css
│   │   ├── products.css
│   │   ├── styles.css
│   │   └── users.css
│   ├── WEB-INF/       # Configurações web
│   ├── META-INF/      # Configurações META
│   ├── index.xhtml    # Dashboard principal
│   ├── payments.xhtml # Gestão de pagamentos
│   ├── products.xhtml # Gestão de produtos
│   └── users.xhtml    # Gestão de usuários
├── sql/               # Scripts de banco de dados
│   ├── 01_create_tables.sql
│   └── 02_sample_data.sql
├── docker-compose.yml # Configuração Docker
├── pom.xml           # Configuração Maven
└── README.md
```

## 🗄️ Modelo de Dados

### Entidades Principais

#### User (Usuário)
- `id`: Identificador único (BIGSERIAL)
- `name`: Nome do usuário (VARCHAR 100)
- `email`: Email único (VARCHAR 100)

#### Product (Produto)
- `code`: Código único do produto (VARCHAR 50)
- `description`: Descrição do produto (VARCHAR 200)
- `entry_date`: Data de entrada no estoque (DATE)
- `expiry_date`: Data de validade (DATE)
- `quantity`: Quantidade em estoque (NUMERIC 10,2)

#### Payment (Pagamento)
- `id`: Identificador único (BIGSERIAL)
- `product_code`: Código do produto (FK)
- `paid_quantity`: Quantidade paga (NUMERIC 10,2)
- `delivery_date`: Data de entrega (DATE)
- `user_id`: ID do usuário (FK)
- `payment_date`: Data/hora do pagamento (TIMESTAMP)

## 🎯 Funcionalidades

### 📊 Dashboard
- **Estatísticas em Tempo Real**: Total de usuários, produtos e produtos vencidos
- **Status do Sistema**: Monitoramento de conectividade e saúde da aplicação
- **Ações Rápidas**: Navegação direta para cadastros
- **Interface Responsiva**: Design moderno com CSS customizado

### 👥 Gestão de Usuários
- **CRUD Completo**: Criar, visualizar, editar e excluir usuários
- **Validação de Email**: Verificação de email único
- **Busca e Filtros**: Pesquisa por nome e email
- **Paginação**: Navegação por páginas

### 📦 Gestão de Produtos
- **Controle de Estoque**: Cadastro com quantidade e datas
- **Controle de Validade**: Monitoramento automático de produtos vencidos
- **Alertas Inteligentes**: Produtos próximos ao vencimento (7 dias)
- **Busca Avançada**: Filtros por código, descrição e status
- **Validações**: Verificação de datas e quantidades

### 💰 Gestão de Pagamentos
- **Registro de Transações**: Pagamentos com produtos e usuários
- **Controle de Estoque**: Atualização automática de quantidades
- **Validação de Produtos**: Apenas produtos válidos (com estoque e não vencidos)
- **Histórico Completo**: Listagem com paginação e filtros
- **Relacionamentos**: Integração com produtos e usuários

## 🛠️ Configuração e Instalação

### Pré-requisitos
- Java 8 ou superior
- Maven 3.6+
- Docker e Docker Compose
- PostgreSQL 13 (opcional, se não usar Docker)

### Instalação com Docker (Recomendado)

1. **Clone o repositório**
```bash
git clone https://github.com/seu-usuario/estoque-facil.git
cd estoque-facil
```

2. **Inicie os serviços**
```bash
docker-compose up -d
```

3. **Acesse a aplicação**
- **Aplicação**: http://localhost:8080/estoque-facil
- **pgAdmin**: http://localhost:8081
  - Email: admin@estoquefacil.com
  - Senha: admin123

### Instalação Manual

1. **Configure o banco de dados**
```bash
# Execute os scripts SQL
psql -U postgres -d estoque_facil -f sql/01_create_tables.sql
psql -U postgres -d estoque_facil -f sql/02_sample_data.sql
```

2. **Configure o Hibernate**
```xml
<!-- src/main/resources/hibernate.cfg.xml -->
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/estoque_facil</property>
<property name="hibernate.connection.username">postgres</property>
<property name="hibernate.connection.password">postgres</property>
```

3. **Compile e execute**
```bash
mvn clean package
# Deploy o WAR em um servidor Java EE (Tomcat, WildFly, etc.)
```

## 🔧 Configurações

### Banco de Dados
- **Host**: localhost
- **Porta**: 5432
- **Database**: estoque_facil
- **Usuário**: postgres
- **Senha**: postgres

### Pool de Conexões (C3P0)
- **Mínimo**: 5 conexões
- **Máximo**: 20 conexões
- **Timeout**: 300 segundos
- **Teste de Idle**: 3000 segundos

### Aplicação
- **Context Path**: /estoque-facil
- **Encoding**: UTF-8
- **Java Version**: 1.8

## 🎨 Interface e UX

### Design System
- **Framework CSS**: PrimeFaces 10.0.0
- **Tema**: Customizado com gradientes modernos
- **Responsividade**: Layout adaptativo
- **Acessibilidade**: ARIA labels e navegação por teclado

### Componentes Principais
- **Sidebar Navigation**: Menu lateral com ícones
- **Data Tables**: Tabelas com paginação e ordenação
- **Forms**: Formulários com validação
- **Messages**: Sistema de notificações
- **Panels**: Containers colapsáveis

## 🔒 Segurança e Validações

### Validações de Negócio
- **Produtos**: Verificação de validade e estoque
- **Usuários**: Email único obrigatório
- **Pagamentos**: Quantidade disponível em estoque
- **Datas**: Validação de datas futuras

### Tratamento de Erros
- **Exceções Customizadas**: BusinessException e DAOException
- **Mensagens de Usuário**: Feedback claro e informativo
- **Logs**: Rastreamento de erros
- **Recuperação**: Mecanismos de retry

## 📈 Performance

### Otimizações Implementadas
- **Lazy Loading**: Carregamento sob demanda de dados
- **Connection Pool**: C3P0 para gerenciamento de conexões
- **Índices de Banco**: Otimização de consultas
- **Paginação**: Limitação de resultados
- **Cache**: Reutilização de objetos

### Monitoramento
- **Health Checks**: Verificação de conectividade
- **Estatísticas**: Métricas de uso
- **Logs**: Rastreamento de operações

## 🧪 Testes

### Dados de Exemplo
O sistema inclui dados de exemplo para demonstração:
- **5 Usuários**: João, Maria, Pedro, Ana, Carlos
- **5 Produtos**: Arroz, Feijão, Macarrão, Óleo, Farinha
- **5 Pagamentos**: Transações de exemplo

### Cenários de Teste
1. **Cadastro de Produtos**: Validação de datas e quantidades
2. **Controle de Estoque**: Atualização automática
3. **Pagamentos**: Validação de produtos válidos
4. **Dashboard**: Carregamento de estatísticas

## 🚀 Deploy

### Docker Compose
```bash
# Desenvolvimento
docker-compose up -d

# Produção
docker-compose -f docker-compose.prod.yml up -d
```

### Servidor de Aplicação
- **Tomcat**: ou 9.0+





