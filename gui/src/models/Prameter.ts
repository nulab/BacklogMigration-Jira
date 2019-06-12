export type DomainType =
  | ''
  | '.backlog.com'
  | '.backlog.jp'
  | '.backlogtool.com';

export interface Parameters {
  spaceId: string;
  domain: DomainType;
  apiKey: string;
  projectKey: string;
}

export const Parameters = (
  spaceId: string,
  domain: DomainType,
  apiKey: string,
  projectKey: string
) => ({
  spaceId,
  domain,
  apiKey,
  projectKey,
});
