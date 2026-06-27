"use client";

import Link from "next/link";

interface PrivacySection {
  id: string;
  title: string;
  content: React.ReactNode;
}

const privacySections: PrivacySection[] = [
  {
    id: "coleta",
    title: "1. Dados que coletamos",
    content: (
      <>
        <p className="text-gray-600 text-sm leading-relaxed mb-3">
          Ao usar o MoVa, podemos coletar os seguintes dados:
        </p>
        <ul className="list-disc list-inside space-y-1.5 text-gray-600 text-sm leading-relaxed">
          <li>
            <span className="font-semibold text-gray-700">
              Dados de cadastro:
            </span>{" "}
            nome, e-mail e senha (armazenada de forma criptografada);
          </li>
          <li>
            <span className="font-semibold text-gray-700">Login social:</span>{" "}
            nome e e-mail fornecidos pelo Google, caso você use essa opção;
          </li>
          <li>
            <span className="font-semibold text-gray-700">
              Dados de uso:
            </span>{" "}
            eventos visualizados, buscas realizadas e interações na plataforma;
          </li>
          <li>
            <span className="font-semibold text-gray-700">
              Dados de dispositivo:
            </span>{" "}
            tipo de navegador, sistema operacional e endereço IP.
          </li>
        </ul>
      </>
    ),
  },
  {
    id: "uso",
    title: "2. Como usamos seus dados",
    content: (
      <>
        <p className="text-gray-600 text-sm leading-relaxed mb-3">
          Utilizamos suas informações exclusivamente para:
        </p>
        <ul className="list-disc list-inside space-y-1.5 text-gray-600 text-sm leading-relaxed">
          <li>Autenticar seu acesso e manter sua sessão ativa;</li>
          <li>
            Personalizar sua experiência com eventos relevantes para você;
          </li>
          <li>Enviar notificações sobre eventos do seu interesse;</li>
          <li>Melhorar as funcionalidades e o desempenho da plataforma;</li>
          <li>Cumprir obrigações legais quando necessário.</li>
        </ul>
      </>
    ),
  },
  {
    id: "compartilhamento",
    title: "3. Compartilhamento de dados",
    content: (
      <>
        <p className="text-gray-600 text-sm leading-relaxed mb-3">
          Não vendemos nem alugamos seus dados pessoais. Podemos compartilhá-los
          apenas nas seguintes situações:
        </p>
        <ul className="list-disc list-inside space-y-1.5 text-gray-600 text-sm leading-relaxed">
          <li>
            Com prestadores de serviço que nos auxiliam na operação da
            plataforma (ex: hospedagem e autenticação), sempre sob contrato de
            confidencialidade;
          </li>
          <li>
            Quando exigido por lei, ordem judicial ou autoridade competente.
          </li>
        </ul>
      </>
    ),
  },
  {
    id: "armazenamento",
    title: "4. Armazenamento e segurança",
    content: (
      <p className="text-gray-600 text-sm leading-relaxed">
        Seus dados são armazenados em servidores seguros e protegidos por
        criptografia. Adotamos boas práticas de segurança para prevenir acesso
        não autorizado, alteração ou destruição das informações. Ainda assim,
        nenhum sistema é 100% inviolável — em caso de incidente, você será
        notificado.
      </p>
    ),
  },
  {
    id: "cookies",
    title: "5. Cookies e armazenamento local",
    content: (
      <p className="text-gray-600 text-sm leading-relaxed">
        Utilizamos o armazenamento local do navegador (localStorage) para manter
        sua sessão ativa após o login. Não utilizamos cookies de rastreamento
        para fins publicitários. Você pode limpar esses dados a qualquer momento
        nas configurações do seu navegador, o que encerrará sua sessão.
      </p>
    ),
  },
  {
    id: "direitos",
    title: "6. Seus direitos",
    content: (
      <>
        <p className="text-gray-600 text-sm leading-relaxed mb-3">
          Você tem direito a:
        </p>
        <ul className="list-disc list-inside space-y-1.5 text-gray-600 text-sm leading-relaxed">
          <li>Acessar os dados que temos sobre você;</li>
          <li>Corrigir informações incorretas ou desatualizadas;</li>
          <li>
            Solicitar a exclusão da sua conta e dos seus dados pessoais;
          </li>
          <li>Revogar o consentimento para o uso dos seus dados.</li>
        </ul>
        <p className="text-gray-600 text-sm leading-relaxed mt-3">
          Para exercer qualquer um desses direitos, entre em contato pelo e-mail
          abaixo.
        </p>
      </>
    ),
  },
  {
    id: "menores",
    title: "7. Menores de idade",
    content: (
      <p className="text-gray-600 text-sm leading-relaxed">
        O MoVa não é destinado a crianças menores de 13 anos. Não coletamos
        intencionalmente dados de menores dessa faixa etária. Se identificarmos
        um cadastro nessa situação, a conta será removida.
      </p>
    ),
  },
  {
    id: "alteracoes",
    title: "8. Alterações nesta política",
    content: (
      <p className="text-gray-600 text-sm leading-relaxed">
        Esta Política de Privacidade pode ser atualizada periodicamente.
        Notificaremos você por e-mail ou aviso na plataforma em caso de
        mudanças relevantes. O uso contínuo do MoVa após as alterações indica
        que você aceita a nova versão.
      </p>
    ),
  },
];

export default function PrivacyPage() {
  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 font-sans">
      <div className="max-w-2xl mx-auto bg-white rounded-xl border border-gray-200 shadow-sm p-8 md:p-12">

        {/* Header */}
        <div className="mb-8 pb-6 border-b border-gray-200">
          <span className="inline-block text-xs font-semibold text-gray-500 bg-gray-100 border border-gray-200 rounded-md px-2.5 py-1 mb-4 tracking-wide">
            Documento legal
          </span>
          <h1 className="text-4xl font-bold text-[#b91c1c] mb-1">MoVa</h1>
          <h2 className="text-xl font-semibold text-gray-800 mb-2">
            Política de Privacidade
          </h2>
          <p className="text-xs text-gray-400">
            Última atualização: junho de 2026 &nbsp;·&nbsp; Versão 1.0
          </p>
        </div>

        {/* Intro */}
        <p className="text-sm text-gray-600 leading-relaxed mb-2">
          A sua privacidade é importante para nós. Esta política explica quais
          dados o{" "}
          <span className="font-semibold text-gray-800">MoVa</span> coleta,
          como os utiliza e quais são os seus direitos em relação a essas
          informações.
        </p>

        {/* Sections */}
        <div className="mt-6 space-y-6">
          {privacySections.map((section) => (
            <div key={section.id}>
              <div className="border-t border-gray-100 pt-6">
                <h3 className="text-sm font-semibold text-gray-800 mb-3">
                  {section.title}
                </h3>
                {section.content}
              </div>
            </div>
          ))}
        </div>

        {/* Footer */}
        <div className="mt-8 p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-sm text-gray-600 leading-relaxed">
            Dúvidas sobre esta política ou sobre seus dados? Entre em contato
            pelo e-mail{" "}
            <a
              href="mailto:contato@mova.com.br"
              className="font-medium text-[#b91c1c] hover:underline"
            >
              contato@mova.com.br
            </a>
            .
          </p>
        </div>

        {/* Back link */}
        <div className="mt-6 text-center">
          <Link
            href="/register"
            className="text-sm text-[#0066cc] hover:underline font-medium"
          >
            ← Voltar para o cadastro
          </Link>
        </div>

      </div>
    </div>
  );
}