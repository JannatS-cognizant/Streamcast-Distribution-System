export interface Title {
  id?: number;
  name: string;
  releaseDate: string;
  genre: string;
  language: string;
  status: string;
}

export interface Asset {
  id?: number;
  assetType: string;
  fileURI: string;
  checksum: string;
  duration: number;
  status: string;
  titleId: number;
  titleName?: string;
}

export interface Metadata {
  id?: number;
  key: string;
  value: string;
  titleDTO?: { id: number; name: string };
}
