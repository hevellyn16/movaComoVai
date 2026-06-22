export interface Venue {
  id: string;
  name: string;
  number: string;
  city: string;
  street: string;
  neighborhood: string;
  landmark?: string;
  hasParkingLot: boolean;
  hasAccessibility: boolean;
  hasBathroom: boolean;
  hasFoodsAndDrinks: boolean;
}

export type VenueResponseDTO = Venue;

export interface VenueCreateDTO {
  name: string;
  number: string;
  city: string;
  street: string;
  neighborhood: string;
  landmark?: string;
  hasParkingLot: boolean;
  hasAccessibility: boolean;
  hasBathroom: boolean;
  hasFoodsAndDrinks: boolean;
}

export interface VenueUpdateDTO {
  name?: string;
  number?: string;
  city?: string;
  street?: string;
  neighborhood?: string;
  landmark?: string;
  hasParkingLot?: boolean;
  hasAccessibility?: boolean;
  hasBathroom?: boolean;
  hasFoodsAndDrinks?: boolean;
}

export interface VenueSearchFilters {
  name?: string;
  city?: string;
  neighborhood?: string;
}