"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/hooks/useAuth";
import { useUser } from "@/hooks/useUsers";
import { useTags } from "@/hooks/useTags";

export default function ConfiguracoesPage() {
  const router = useRouter();

  // Hooks de API
  const { user, logout } = useAuth();
  const {
    fetchUserById,
    updateProfile,
    deleteAccount,
    addTags,
    removeTag,
    isLoading: isUserLoading,
  } = useUser();
  const {
    fetchAllTags,
    tags: availableTags,
    isLoading: isTagsLoading,
  } = useTags();

  // Estados Locais (Formulário)
  const [name, setName] = useState("");
  const [bio, setBio] = useState("");
  const [selectedInterestIds, setSelectedInterestIds] = useState<string[]>([]);

  // Estados de UI
  const [isSaving, setIsSaving] = useState(false);
  const [privateAccount, setPrivateAccount] = useState(false);
  const [activeStatus, setActiveStatus] = useState(true);

  // =======================================================================
  // CORREÇÃO DO LOOP: Dependendo apenas do ID do usuário logado
  // =======================================================================
  useEffect(() => {
    if (!user?.id) return; // Se o ID do usuário ainda não chegou, não faz nada

    const loadData = async () => {
      // Executa a busca de tags
      fetchAllTags();

      try {
        const userData = await fetchUserById(user.id);
        setName(userData.name || "");

        // Se o seu DTO já suportar tags no usuário, descomente abaixo:
        // if (userData.tags) {
        //     setSelectedInterestIds(userData.tags.map((t: any) => t.id));
        // }
      } catch (error) {
        console.error("Falha ao carregar perfil:", error);
      }
    };

    loadData();

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user?.id]); // Roda apenas quando o ID do usuário for resolvido

  // Função de alternar interesses (Otimista)
  const toggleInterest = async (tagId: string) => {
    const isSelected = selectedInterestIds.includes(tagId);

    setSelectedInterestIds((current) =>
      isSelected ? current.filter((id) => id !== tagId) : [...current, tagId],
    );

    try {
      if (isSelected) {
        await removeTag(tagId);
      } else {
        await addTags([tagId]);
      }
    } catch (error) {
      console.error("Erro ao atualizar interesse:", error);
      setSelectedInterestIds((current) =>
        isSelected ? [...current, tagId] : current.filter((id) => id !== tagId),
      );
    }
  };

  // Função de salvar o perfil
  const handleSaveProfile = async () => {
    setIsSaving(true);
    try {
      await updateProfile({ name });
      alert("Perfil updated com sucesso!");
    } catch (error) {
      console.error("Erro ao salvar:", error);
      alert("Falha ao salvar as alterações.");
    } finally {
      setIsSaving(false);
    }
  };

  // Função de exclusão de conta
  const handleDeleteAccount = async () => {
    const confirmDelete = window.confirm(
      "Você tem certeza absoluta? Isso excluirá sua conta permanentemente. Esta ação não pode ser desfeita.",
    );

    if (confirmDelete) {
      try {
        await deleteAccount();
        alert("Conta excluída com sucesso.");
        logout();
      } catch (error) {
        console.error("Erro ao excluir conta:", error);
        alert("Não foi possível excluir a conta no momento.");
      }
    }
  };

  // Exibe o carregamento inicial apenas se estiver buscando e os dados locais estiverem vazios
  if ((isUserLoading || isTagsLoading) && !name) {
    return (
      <div className="flex-1 min-h-screen bg-[#F8F6F3] flex items-center justify-center">
        <div className="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-[#b91c1c]"></div>
      </div>
    );
  }

  return (
    <main className="flex-1 p-6 bg-[#F8F6F3] min-h-screen">
      <div className="mx-auto max-w-screen-2xl">
        {/* Header Superior */}
        <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Configurações</h1>
            <p className="text-sm text-gray-500">
              Ajuste sua conta, preferências de privacidade e notificações.
            </p>
          </div>

          <button
            type="button"
            onClick={handleSaveProfile}
            disabled={isSaving}
            className={`cursor-pointer inline-flex items-center justify-center rounded-full px-5 py-3 text-sm font-semibold text-white shadow-sm transition-colors ${
              isSaving ? "bg-gray-400" : "bg-[#b91c1c] hover:bg-[#991b1b]"
            }`}
          >
            {isSaving ? "Salvando..." : "Salvar Perfil"}
          </button>
        </div>

        <div className="grid gap-6 xl:grid-cols-[minmax(0,1fr)_320px]">
          {/* Coluna Principal */}
          <div className="space-y-6">
            {/* Seção: Perfil Público */}
            <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
              <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                <div>
                  <h2 className="text-xl font-semibold text-gray-900">
                    Perfil Público
                  </h2>
                  <p className="text-sm text-gray-500">
                    Atualize a exibição do seu perfil e suas informações
                    públicas.
                  </p>
                </div>
                <div className="flex items-center gap-4">
                  <div className="relative h-20 w-20 overflow-hidden rounded-full border border-gray-200 bg-gray-100 flex items-center justify-center">
                    <span className="text-2xl font-bold text-gray-400">
                      {name?.charAt(0)?.toUpperCase() || "?"}
                    </span>
                    <div
                      className="absolute bottom-0 right-0 rounded-full bg-[#b91c1c] p-1 text-white shadow-md cursor-pointer hover:bg-[#991b1b]"
                      onClick={() =>
                        alert("Upload de avatar disponível em breve!")
                      }
                    >
                      <span className="material-symbols-outlined text-[18px]">
                        camera_alt
                      </span>
                    </div>
                  </div>
                  <span className="text-sm text-gray-500">
                    PNG ou JPG (Máx. 5MB)
                  </span>
                </div>
              </div>

              <div className="mt-6 grid gap-4 sm:grid-cols-2">
                <label className="space-y-2 text-sm font-medium text-gray-700">
                  Nome de Exibição
                  <input
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="Seu nome completo"
                    className="w-full rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-[#b91c1c] focus:ring-2 focus:ring-red-100"
                  />
                </label>
                <label className="space-y-2 text-sm font-medium text-gray-700">
                  E-mail (Apenas leitura)
                  <input
                    type="email"
                    value={user?.email || ""}
                    disabled
                    className="w-full rounded-2xl border border-gray-200 bg-gray-100 px-4 py-3 text-sm text-gray-500 outline-none cursor-not-allowed"
                  />
                </label>
              </div>

              <div className="mt-4 space-y-4">
                <label className="space-y-2 text-sm font-medium text-gray-700">
                  Biografia Curta
                  <textarea
                    rows={4}
                    value={bio}
                    onChange={(e) => setBio(e.target.value)}
                    placeholder="Conte um pouco sobre você e seus interesses culturais..."
                    className="w-full resize-none rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-900 outline-none transition focus:border-[#b91c1c] focus:ring-2 focus:ring-red-100"
                  />
                </label>
                <label className="space-y-2 text-sm font-medium text-gray-700">
                  Localização (Apenas leitura)
                  <input
                    type="text"
                    defaultValue="Sobral, Ceará"
                    disabled
                    className="w-full rounded-2xl border border-gray-200 bg-gray-100 px-4 py-3 text-sm text-gray-500 outline-none cursor-not-allowed"
                  />
                </label>
              </div>
            </section>

            {/* Seção: Interesses */}
            <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900">
                    Interesses
                  </h3>
                  <p className="text-sm text-gray-500">
                    Selecione categorias para personalizar seu feed cultural.
                    (Salvo automaticamente)
                  </p>
                </div>
                <span className="rounded-full bg-gray-100 px-3 py-1 text-xs font-semibold uppercase tracking-[0.15em] text-gray-500">
                  {selectedInterestIds.length}
                </span>
              </div>

              <div className="mt-6 flex flex-wrap gap-3">
                {availableTags.map((tag) => {
                  const selected = selectedInterestIds.includes(tag.id);
                  return (
                    <button
                      key={tag.id}
                      type="button"
                      onClick={() => toggleInterest(tag.id)}
                      className={`cursor-pointer rounded-full border px-4 py-2 text-sm font-medium transition-colors ${
                        selected
                          ? "border-[#b91c1c] bg-[#b91c1c] text-white"
                          : "border-gray-200 bg-gray-50 text-gray-700 hover:border-[#b91c1c] hover:text-[#b91c1c]"
                      }`}
                    >
                      {tag.tagName}
                    </button>
                  );
                })}
              </div>
            </section>
          </div>

          {/* Coluna Lateral */}
          <div>
            {/* Privacidade */}
            <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm mb-6">
              <h3 className="text-lg font-semibold text-gray-900">
                Privacidade
              </h3>
              <p className="mt-2 text-sm text-gray-500">
                Controle quem vê seu perfil e sua atividade.
              </p>

              <div className="mt-6 space-y-4">
                <div className="flex items-center justify-between rounded-3xl border border-gray-200 bg-gray-50 px-4 py-4">
                  <div>
                    <p className="text-sm font-semibold text-gray-900">
                      Conta Privada
                    </p>
                    <p className="text-xs text-gray-500">
                      Apenas seguidores podem ver seus itens.
                    </p>
                  </div>
                  <button
                    type="button"
                    onClick={() => setPrivateAccount((prev) => !prev)}
                    className={`cursor-pointer relative inline-flex h-7 w-14 shrink-0 items-center rounded-full p-1 transition-colors duration-200 ${
                      privateAccount ? "bg-[#b91c1c]" : "bg-gray-300"
                    }`}
                  >
                    <span
                      className={`inline-block h-5 w-5 transform rounded-full bg-white shadow transition-transform duration-200 ease-in-out ${
                        privateAccount ? "translate-x-7" : "translate-x-0"
                      }`}
                    />
                  </button>
                </div>

                <div className="flex items-center justify-between rounded-3xl border border-gray-200 bg-gray-50 px-4 py-4">
                  <div>
                    <p className="text-sm font-semibold text-gray-900">
                      Status de Atividade
                    </p>
                    <p className="text-xs text-gray-500">
                      Permite que vejam quando você está online.
                    </p>
                  </div>
                  <button
                    type="button"
                    onClick={() => setActiveStatus((prev) => !prev)}
                    className={`cursor-pointer relative inline-flex h-7 w-14 shrink-0 items-center rounded-full p-1 transition-colors duration-200 ${
                      activeStatus ? "bg-[#b91c1c]" : "bg-gray-300"
                    }`}
                  >
                    <span
                      className={`inline-block h-5 w-5 transform rounded-full bg-white shadow transition-transform duration-200 ease-in-out ${
                        activeStatus ? "translate-x-7" : "translate-x-0"
                      }`}
                    />
                  </button>
                </div>
              </div>
            </section>

            {/* Zona de Perigo */}
            <section className="rounded-3xl border border-gray-200 bg-white p-6 shadow-sm">
              <h3 className="text-lg font-semibold text-gray-900">
                Zona de Perigo
              </h3>
              <p className="mt-2 text-sm text-gray-500">
                Ações permanentes que não podem ser desfeitas.
              </p>

              <div className="mt-6 flex flex-col gap-3">
                <button
                  type="button"
                  onClick={() => alert("Função em desenvolvimento.")}
                  className="cursor-pointer rounded-full border border-gray-200 bg-white px-4 py-3 text-sm font-semibold text-gray-700 hover:bg-gray-50 transition"
                >
                  Desativar Temporariamente
                </button>
                <button
                  type="button"
                  onClick={handleDeleteAccount}
                  className="cursor-pointer rounded-full border border-[#b91c1c] px-4 py-3 text-sm font-semibold text-[#b91c1c] hover:bg-red-50 transition"
                >
                  Excluir Conta
                </button>
              </div>
            </section>

            {/* Botão de Sair */}
            <div className="mt-6 flex justify-end">
              <button
                type="button"
                onClick={logout}
                className="cursor-pointer rounded-full bg-white border border-[#b91c1c] px-8 py-3 text-base font-semibold text-[#b91c1c] transition hover:bg-[#b91c1c] hover:text-white shadow-sm w-full md:w-auto"
              >
                Sair da Conta
              </button>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
