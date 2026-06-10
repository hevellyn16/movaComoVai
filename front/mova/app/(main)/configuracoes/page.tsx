"use client";

import { useState } from "react";

const interests = [
  "Música",
  "Teatro",
  "Artes Visuais",
  "Gastronomia",
  "Cinema",
  "Literatura",
  "Dança",
];

export default function ConfiguracoesPage() {
  const [privateAccount, setPrivateAccount] = useState(false);
  const [activeStatus, setActiveStatus] = useState(true);
  const [emailEvents, setEmailEvents] = useState(true);
  const [pushNotifications, setPushNotifications] = useState(false);
  const [selectedInterests, setSelectedInterests] = useState<string[]>([
    "Música",
    "Artes Visuais",
    "Literatura",
  ]);

  const toggleInterest = (interest: string) => {
    setSelectedInterests((current) => {
      if (current.includes(interest)) {
        return current.filter((item) => item !== interest);
      }
      return [...current, interest];
    });
  };

  return (
    <main className="flex-1 p-6 bg-[#F8F6F3] min-h-screen">
      <div className="mx-auto max-w-screen-2xl">
        <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-2xl font-bold text-mova-dark">Configurações</h1>
            <p className="text-sm text-gray-500">Ajuste sua conta, preferências de privacidade e notificações.</p>
          </div>

          <button
            type="button"
            className="inline-flex items-center justify-center rounded-full bg-mova-red px-5 py-3 text-sm font-semibold text-white shadow-sm transition-colors hover:bg-mova-red-dark"
          >
            Salvar Perfil
          </button>
        </div>

        <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_320px]">
          <div className="space-y-6">
            <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
              <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div>
                  <h2 className="text-xl font-semibold text-mova-dark">Perfil Público</h2>
                  <p className="text-sm text-gray-500">Atualize a exibição do seu perfil e suas informações públicas.</p>
                </div>
                <div className="flex items-center gap-4">
                  <div className="relative h-20 w-20 overflow-hidden rounded-full border border-gray-200 bg-gray-100">
                    <span className="material-symbols-outlined text-4xl text-gray-400">person</span>
                    <div className="absolute bottom-0 right-0 rounded-full bg-mova-red p-1 text-white shadow-md">
                      <span className="material-symbols-outlined text-[18px]">camera_alt</span>
                    </div>
                  </div>
                  <span className="text-sm text-gray-500">PNG ou JPG (Máx. 5MB)</span>
                </div>
              </div>

              <div className="mt-6 grid gap-4 sm:grid-cols-2">
                <label className="space-y-2 text-sm font-medium text-gray-700">
                  Nome de Exibição
                  <input
                    type="text"
                    defaultValue="MoVa Sobral"
                    className="w-full rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-mova-red focus:ring-2 focus:ring-mova-yellow/20"
                  />
                </label>
                <label className="space-y-2 text-sm font-medium text-gray-700">
                  Username
                  <input
                    type="text"
                    defaultValue="@mova_sobral"
                    className="w-full rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-mova-red focus:ring-2 focus:ring-mova-yellow/20"
                  />
                </label>
              </div>

              <div className="mt-4 space-y-4">
                <label className="space-y-2 text-sm font-medium text-gray-700">
                  Biografia Curta
                  <textarea
                    rows={4}
                    defaultValue="Explorador de cultura local e amante das artes em Sobral. Sempre em busca do próximo evento vibrante."
                    className="w-full resize-none rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-mova-red focus:ring-2 focus:ring-mova-yellow/20"
                  />
                </label>
                <label className="space-y-2 text-sm font-medium text-gray-700">
                  Localização
                  <input
                    type="text"
                    defaultValue="Sobral, Ceará"
                    className="w-full rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-mova-red focus:ring-2 focus:ring-mova-yellow/20"
                  />
                </label>
              </div>
            </section>

            <div className="grid gap-6 lg:grid-cols-2">
              <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
                <h3 className="text-lg font-semibold text-mova-dark">Privacidade</h3>
                <p className="mt-2 text-sm text-gray-500">Controle quem vê seu perfil e sua atividade.</p>

                <div className="mt-6 space-y-4">
                  <label className="flex items-center justify-between rounded-3xl border border-gray-200 bg-gray-50 px-4 py-4">
                    <div>
                      <p className="text-sm font-semibold text-mova-dark">Conta Privada</p>
                      <p className="text-xs text-gray-500">Apenas seguidores podem ver seus itens e eventos.</p>
                    </div>
                    <button
                      type="button"
                      onClick={() => setPrivateAccount(!privateAccount)}
                      className={`relative inline-flex h-7 w-14 items-center rounded-full p-1 transition ${
                        privateAccount ? "bg-mova-red" : "bg-gray-300"
                      }`}
                    >
                      <span
                        className={`inline-block h-5 w-5 transform rounded-full bg-white transition ${
                          privateAccount ? "translate-x-7" : "translate-x-0"
                        }`}
                      />
                    </button>
                  </label>

                  <label className="flex items-center justify-between rounded-3xl border border-gray-200 bg-gray-50 px-4 py-4">
                    <div>
                      <p className="text-sm font-semibold text-mova-dark">Status de Atividade</p>
                      <p className="text-xs text-gray-500">Permite que outros usuários vejam quando você está online.</p>
                    </div>
                    <button
                      type="button"
                      onClick={() => setActiveStatus(!activeStatus)}
                      className={`relative inline-flex h-7 w-14 items-center rounded-full p-1 transition ${
                        activeStatus ? "bg-mova-red" : "bg-gray-300"
                      }`}
                    >
                      <span
                        className={`inline-block h-5 w-5 transform rounded-full bg-white transition ${
                          activeStatus ? "translate-x-7" : "translate-x-0"
                        }`}
                      />
                    </button>
                  </label>
                </div>
              </section>

              <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
                <h3 className="text-lg font-semibold text-mova-dark">Notificações</h3>
                <p className="mt-2 text-sm text-gray-500">Escolha como deseja receber avisos sobre eventos.</p>

                <div className="mt-6 space-y-4">
                  <label className="flex items-center justify-between rounded-3xl border border-gray-200 bg-gray-50 px-4 py-4">
                    <div>
                      <p className="text-sm font-semibold text-mova-dark">E-mails de Eventos</p>
                      <p className="text-xs text-gray-500">Receba novidades direto na sua caixa de entrada.</p>
                    </div>
                    <button
                      type="button"
                      onClick={() => setEmailEvents(!emailEvents)}
                      className={`relative inline-flex h-7 w-14 items-center rounded-full p-1 transition ${
                        emailEvents ? "bg-mova-red" : "bg-gray-300"
                      }`}
                    >
                      <span
                        className={`inline-block h-5 w-5 transform rounded-full bg-white transition ${
                          emailEvents ? "translate-x-7" : "translate-x-0"
                        }`}
                      />
                    </button>
                  </label>

                  <label className="flex items-center justify-between rounded-3xl border border-gray-200 bg-gray-50 px-4 py-4">
                    <div>
                      <p className="text-sm font-semibold text-mova-dark">Notificações Push</p>
                      <p className="text-xs text-gray-500">Ative avisos instantâneos no seu aparelho.</p>
                    </div>
                    <button
                      type="button"
                      onClick={() => setPushNotifications(!pushNotifications)}
                      className={`relative inline-flex h-7 w-14 items-center rounded-full p-1 transition ${
                        pushNotifications ? "bg-mova-red" : "bg-gray-300"
                      }`}
                    >
                      <span
                        className={`inline-block h-5 w-5 transform rounded-full bg-white transition ${
                          pushNotifications ? "translate-x-7" : "translate-x-0"
                        }`}
                      />
                    </button>
                  </label>
                </div>
              </section>
            </div>

            <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
              <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <h3 className="text-lg font-semibold text-mova-dark">Zona de Perigo</h3>
                  <p className="mt-2 text-sm text-gray-500">Ações permanentes que não podem ser desfeitas.</p>
                </div>
                <div className="flex flex-col gap-2 sm:flex-row">
                  <button
                    type="button"
                    className="rounded-full border border-gray-200 bg-white px-4 py-3 text-sm font-semibold text-gray-700 hover:bg-gray-50 transition"
                  >
                    Desativar Temporariamente
                  </button>
                  <button
                    type="button"
                    className="rounded-full bg-mova-red px-4 py-3 text-sm font-semibold text-white transition hover:bg-mova-red-dark"
                  >
                    Excluir Conta
                  </button>
                </div>
              </div>
            </section>
          </div>

          <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="flex items-center justify-between gap-3">
              <div>
                <h3 className="text-lg font-semibold text-mova-dark">Interesses</h3>
                <p className="text-sm text-gray-500">Selecione categorias para personalizar seu feed cultural.</p>
              </div>
              <span className="rounded-full bg-gray-100 px-3 py-1 text-xs font-semibold uppercase tracking-[0.15em] text-gray-500">
                {selectedInterests.length}
              </span>
            </div>

            <div className="mt-6 flex flex-wrap gap-3">
              {interests.map((interest) => {
                const selected = selectedInterests.includes(interest);
                return (
                  <button
                    key={interest}
                    type="button"
                    onClick={() => toggleInterest(interest)}
                    className={`rounded-full border px-4 py-2 text-sm font-medium transition ${
                      selected
                        ? "border-mova-red bg-mova-red text-white"
                        : "border-gray-200 bg-gray-50 text-gray-700 hover:border-mova-red hover:text-mova-red"
                    }`}
                  >
                    {interest}
                  </button>
                );
              })}
            </div>
          </section>
        </div>
      </div>
    </main>
  );
}
